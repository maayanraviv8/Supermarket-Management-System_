package Storage.Backend.ServiceLayer;

public class Response {
        private String errorMessage;
        private Object returnValue;

        // Constructors
        public Response() {
            this.errorMessage = null;
            this.returnValue = null;
        }

        public Response(String errorMessage) {
            this.errorMessage = errorMessage;
            this.returnValue = null;
        }

        public Response(Object returnValue, boolean success) {
            if (success) {
                this.errorMessage = null;
                this.returnValue = returnValue;
            } else {
                this.errorMessage = "Failed";
                this.returnValue = returnValue;
            }
        }

        // Getters
        public String getErrorMessage() {
            return errorMessage;
        }

        public Object getReturnValue() {
            return returnValue;
        }

        // ToString methods
        @Override
        public String toString() {
            if (this.errorMessage == null) {
                return "success";
            }
            return "failed: " + this.errorMessage;
        }

        public String toStringOpposite() {
            if (this.errorMessage == null) {
                return "false: Test should fail";
            }
            return "true: " + this.errorMessage;
        }
    }

