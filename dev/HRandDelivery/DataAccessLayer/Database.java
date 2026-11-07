package HRandDelivery.DataAccessLayer;


import HRandDelivery.DomainLayer.HR.enums.Role;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single-Connection helper, identical in spirit to the lesson demo.
 */
public final class Database {

    private static final Logger log = Logger.getLogger(Database.class.getName());
    private static final String DB_URL;
    private static Connection conn;

    // Static initializer – runs once when the class is loaded.
    static {
        try {
            // Create path to DB directory and file
            Path dbPath = Paths.get(System.getProperty("user.dir"), "DB", "SuperLee.db");
            Files.createDirectories(dbPath.getParent()); // create DB/ folder if it doesn't exist

            DB_URL = "jdbc:sqlite:" + dbPath.toString().replace("\\", "/");

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            initSchema();
        } catch (ClassNotFoundException | SQLException | java.io.IOException e) {
            log.log(Level.SEVERE, "DB init failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() { /* prevent instantiation */ }

    /** Returns the same live connection (use try-with-resources on statements, not on this). */
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
        }
        return conn;
    }

    private static void initSchema() throws SQLException {
        try (Statement st = conn.createStatement()) {
            // Branches
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS branches(
                        id              TEXT    PRIMARY KEY,
                        branch_name     TEXT    NOT NULL,
                        address         TEXT,
                        contact_name    TEXT,
                        contact_number  TEXT
                    );
                """);

            // Roles
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS roles(
                        name TEXT PRIMARY KEY
                    );
                """);
            // seed enum values
            initRoles(conn);

            // Employees
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employees(
                        id         TEXT PRIMARY KEY,
                        full_name  TEXT NOT NULL,
                        is_active  INTEGER NOT NULL CHECK(is_active IN (0,1)) DEFAULT 1,
                        branch_num TEXT,
                        FOREIGN KEY(branch_num) REFERENCES branches(id)
                    );
                """);

            // BankAccount (one-to-one with Employee)
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS bank_accounts(
                        employee_id    TEXT PRIMARY KEY,
                        account_id     TEXT,
                        bank_id        TEXT,
                        bank_branch_id TEXT,
                        FOREIGN KEY(employee_id) REFERENCES employees(id)
                    );
                """);

            // WorkConditions (one-to-one with Employee)
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS work_conditions(
                        employee_id        TEXT PRIMARY KEY,
                        salary             TEXT,
                        start_date         TEXT,
                        vacation_days_used INTEGER,
                        sick_days_used     INTEGER,
                        funds              TEXT,
                        FOREIGN KEY(employee_id) REFERENCES employees(id)
                    );
                """);

            // Junction table Employee ↔ Roles (many-to-many)
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_roles(
                        employee_id TEXT NOT NULL,
                        role_name   TEXT NOT NULL,
                        PRIMARY KEY(employee_id, role_name),
                        FOREIGN KEY(employee_id) REFERENCES employees(id),
                        FOREIGN KEY(role_name)   REFERENCES roles(name)
                    );
                """);

            //Shifts
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shifts(
                        branch_num           TEXT    NOT NULL,
                        date                 TEXT    NOT NULL,
                        time_slot            TEXT    NOT NULL,
                        start_time           TEXT    NOT NULL,
                        end_time             TEXT    NOT NULL,
                        needed_admin         INTEGER NOT NULL DEFAULT 0,
                        needed_shift_manager INTEGER NOT NULL DEFAULT 0,
                        needed_driver        INTEGER NOT NULL DEFAULT 0,
                        needed_cashier       INTEGER NOT NULL DEFAULT 0,
                        needed_store_keeper  INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(branch_num, date, time_slot),
                        FOREIGN KEY(branch_num) REFERENCES branches(id)
                 );
                """);

            //Junction table Shift ↔ Employee role (many-to-many)
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_assignments(
                        branch_num  TEXT NOT NULL,
                        date        TEXT NOT NULL,
                        time_slot   TEXT NOT NULL,
                        role_name   TEXT NOT NULL,
                        employee_id TEXT NOT NULL,
                        PRIMARY KEY(branch_num, date, time_slot, employee_id),
                        FOREIGN KEY(branch_num)  REFERENCES branches(id),
                        FOREIGN KEY(role_name)   REFERENCES roles(name),
                        FOREIGN KEY(employee_id) REFERENCES employees(id)
                   );
                """);

            //Employees availability for shift
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_availability(
                        branch_num  TEXT NOT NULL,
                        date        TEXT NOT NULL,
                        time_slot   TEXT NOT NULL,
                        employee_id TEXT NOT NULL,
                        PRIMARY KEY(branch_num, date, time_slot, employee_id),
                        FOREIGN KEY(branch_num)  REFERENCES branches(id),
                        FOREIGN KEY(employee_id) REFERENCES employees(id)
                    );
                 """);

            //License (one-to-one with Employee)
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_license(
                        employee_id TEXT    PRIMARY KEY,
                        A           INTEGER NOT NULL CHECK(A IN (0,1)) DEFAULT 0,
                        B           INTEGER NOT NULL CHECK(B IN (0,1)) DEFAULT 0,
                        FOREIGN KEY(employee_id) REFERENCES employees(id)
                    );
                """);

            //Trucks
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS trucks(
                        truck_number TEXT    PRIMARY KEY,
                        model        TEXT    NOT NULL,
                        net_weight   INTEGER NOT NULL,
                        max_weight   INTEGER NOT NULL,
                        license      TEXT    NOT NULL
                    );
                """);

            //Sites
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sites(
                        address          TEXT    PRIMARY KEY,
                        contact_name     TEXT    NOT NULL,
                        contact_number   TEXT    NOT NULL
                    );
                """);

            //Deliveries
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS deliveries(
                        date            TEXT NOT NULL,
                        shift_type      TEXT NOT NULL,
                        driver_id       TEXT NOT NULL,
                        departure_time  TEXT NOT NULL,
                        truck_num       TEXT NOT NULL,
                        source_adss     TEXT NOT NULL,
                        PRIMARY KEY(date, driver_id, departure_time),
                        FOREIGN KEY(driver_id) REFERENCES employees(id),
                        FOREIGN KEY(truck_num) REFERENCES trucks(truck_number),
                        FOREIGN KEY(source_adss) REFERENCES sites(address)
                    );
                """);

            //Delivery destinations
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS delivery_destinations(
                        date                TEXT NOT NULL,
                        driver_id           TEXT NOT NULL,
                        departure_time      TEXT NOT NULL,
                        destination_adss    TEXT NOT NULL,
                        PRIMARY KEY(date, driver_id, destination_adss),
                        FOREIGN KEY(driver_id) REFERENCES employees(id),
                        FOREIGN KEY(destination_adss) REFERENCES sites(address)
                    );
                """);

            //Deadline
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS deadline(
                    deadline_date TEXT NOT NULL
                );
            """);

        }

    }
    private static void initRoles(Connection conn) throws SQLException {
        String sql = "INSERT OR IGNORE INTO roles(name) VALUES (?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Role role : Role.values()) {
                ps.setString(1, role.name());
                ps.executeUpdate();
            }
        }
    }

}



