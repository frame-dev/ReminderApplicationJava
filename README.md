# Reminder Application

A Java-based desktop application for managing reminders and tasks, built with a modern GUI interface.

## Features

- Desktop GUI application
- Multiple database support (MySQL, SQLite, MongoDB)
- Reminder management system
- Audio notifications (using JLayer)
- Logging system (Log4j2)

## Requirements

- Java 21 or higher
- Maven 3.x
- One of the supported databases:
  - MySQL
  - SQLite
  - MongoDB

## Dependencies

The project uses several custom utility libraries:
- SimpleJavaUtils
- JavaGsonUtils
- JavaYamlUtils
- JavaMySQLUtils
- JavaSQLiteUtils
- JavaMongoDBUtils

## Building the Project

```bash
mvn clean package
```

This will create an executable JAR file in the `target` directory.

## Running the Application

```bash
java -jar target/ReminderApplication-1.0-SNAPSHOT.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── ch/framedev/
│   │       ├── database/    # Database related code
│   │       ├── guis/        # GUI components
│   │       ├── main/        # Main application entry point
│   │       ├── utils/       # Utility classes
│   │       ├── classes/     # Core business logic
│   │       └── manager/     # Management classes
│   └── resources/           # Application resources
└── test/                    # Test files
```

## License

This project is licensed under the terms included in the LICENSE file.

## Author

FrameDev
