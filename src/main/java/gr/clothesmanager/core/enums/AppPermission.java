package gr.clothesmanager.core.enums;

public enum AppPermission {
    VIEW {
        @Override
        public String show(){
            return "ΠΡΟΒΟΛΗ";
        }
    },
    EDIT {
        @Override
        public String show() {
            return "ΕΠΕΞΕΡΓΑΣΙΑ";
        }
    },
    DELETE {
        @Override
        public String show() {
            return "ΔΙΑΓΡΑΦΗ";
        }
    };

    public abstract String show();
}
