# Modern Notes App

A feature-rich desktop productivity application built with **JavaFX 21** that combines a task/todo manager with a freeform drawing and notes canvas — all wrapped in a polished, modern UI with custom window chrome and dark/light theme support.

![Java](https://img.shields.io/badge/Java-17+-blue?logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.5-orange)
![Build](https://img.shields.io/badge/build-Maven-green)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)

---

## Features

### Task Management (My Day)
- Add tasks with metadata using inline syntax:
  - `!high` / `!medium` / `!low` — priority tagging
  - `@YYYY-MM-DD` — due dates
  - `#tagname` — categorization tags
- Mark tasks complete (checkbox with strikethrough styling)
- Star/important toggle to flag key tasks
- Search and filter tasks by text and tag
- Auto-sort by priority (high → medium → low → none)
- Persistent local storage (auto-saves to `~/.notesapp/tasks.dat`)

### Drawing & Notes Canvas (My Notes)
- Drawing toolbar: pencil, rectangle, circle, line, and text tool
- Color picker for strokes and shapes
- Sticky notes for quick thoughts
- Drag-to-reposition any shape or note
- Layers panel for managing items
- Export the entire canvas as a PNG image

### Important View
- Dedicated view showing only starred (important) tasks

### Settings
- Theme selector: **Dark**, **Light**, or **System** (auto-detects OS preference)
- Displays app version

### Custom Window Chrome
- Undecorated stage with a custom title bar
- Drag to move, minimize, maximize, and close buttons
- Rounded window corners

### Collapsible Sidebar
- Smooth animated expand/collapse (240px ↔ 64px)
- Navigation: My Day, My Notes, Important, Settings
- Active state highlighting with panel transitions

### System Theme Detection
Automatically detects the OS-level theme on startup:
- **Windows** — reads `AppsUseLightTheme` registry value
- **macOS** — reads `AppleInterfaceStyle` via defaults
- **Linux** — reads GTK theme via `gsettings`

---

## Screenshots

> *Screenshots coming soon*

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK) 17** or later
- **Apache Maven 3.x**

### Clone & Run

```bash
git clone https://github.com/yourusername/Notes-Application-using-JavaFX.git
cd Notes-Application-using-JavaFX

# Compile
mvn compile

# Run
mvn javafx:run

# Package as JAR
mvn package

# Run tests
mvn test
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/shreyaspawar/
│   │   ├── App.java                          # Application entry point
│   │   ├── SystemThemeDetector.java           # OS theme detection
│   │   ├── data/
│   │   │   └── DataManager.java              # Persistent task storage
│   │   ├── model/
│   │   │   └── Task.java                     # Task domain model (JavaFX beans)
│   │   ├── theme/
│   │   │   └── ThemeManager.java             # Dark/Light theme switching
│   │   └── ui/
│   │       ├── NotesPanel.java               # Drawing canvas & sticky notes
│   │       ├── Sidebar.java                  # Collapsible navigation
│   │       ├── TaskPanel.java                # Task list with CRUD
│   │       ├── TitleBar.java                 # Custom window chrome
│   │       └── components/
│   │           ├── CustomDialog.java          # Reusable modal dialogs
│   │           └── TaskCard.java              # Individual task widget
│   └── resources/
│       ├── fonts/                             # Font Awesome 6 & Material Symbols
│       ├── images/                            # App icon, background, splash screen
│       └── styles/
│           ├── base.css                       # Shared/common styles
│           ├── dark-theme.css                 # Dark theme variables
│           └── light-theme.css                # Light theme variables
└── test/java/com/shreyaspawar/
    └── AppTest.java                           # Placeholder test
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17+ | Core language |
| JavaFX | 21.0.5 | Desktop UI framework |
| Maven | 3.x | Build & dependency management |
| Gson | 2.10.1 | JSON serialization (included, available for future use) |
| Font Awesome 6 | — | Icon set |
| Material Symbols | — | Icon set |

---

## Styling

The app uses a modern CSS architecture with three stylesheets:

- **`base.css`** — Universal styles (layout, sidebar, task cards, dialogs, scrollbars, checkboxes, animations, etc.)
- **`dark-theme.css`** — Dark mode color variables
- **`light-theme.css`** — Light mode color variables

Theme switching is handled at runtime by toggling between the dark and light stylesheets on the root `Scene`.

---

## Data Storage

Tasks are persisted locally to `~/.notesapp/tasks.dat` using a custom delimiter-based format. Data is auto-saved on every task addition, deletion, or update — no manual saving required.

---

## License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE.txt) file for details.

---

## Author

**Shreyas Pawar**

---

## Acknowledgements

- [JavaFX](https://openjfx.io/) — Rich client application framework
- [Font Awesome](https://fontawesome.com/) — Icon library
- [Material Symbols](https://fonts.google.com/icons) — Icon library
- [Apache Maven](https://maven.apache.org/) — Build automation
