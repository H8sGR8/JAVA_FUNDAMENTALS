class NoFromCommandException extends Exception {
    NoFromCommandException(String message) {
        super(message);
    }
}

class ItemWithNoSpecifierInManyDatabasesException extends Exception {
    ItemWithNoSpecifierInManyDatabasesException(String message) {
        super(message);
    }
}

class NoSuchDatabaseException extends Exception {
    NoSuchDatabaseException(String message) {
        super(message);
    }
}

class EmptySelectException extends Exception {
    EmptySelectException(String message) {
        super(message);
    }
}

class NotACommandException extends Exception {
    NotACommandException(String message) {
        super(message);
    }
}

class NotEveryBracketClosedOROpenedException extends Exception {
    NotEveryBracketClosedOROpenedException(String message) {
        super(message);
    }
}
