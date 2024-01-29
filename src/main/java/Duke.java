import java.nio.file.*;
import java.util.*;

public class Duke {
    String divider = "--------------------------------------------------";
    ArrayList<Task> taskList = new ArrayList<Task>();
    enum Commands {
        bye, list, mark, unmark, todo, deadline, event, delete
    }

    public String[] splitCommand(String command) {
        String words[] = command.split("\\s+");
        String currentString = "";
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                result.add(words[i]);
                continue;
            }
            if (words[i].charAt(0) == '/') {
                result.add(currentString);
                currentString = "";
                continue;
            }
            currentString += words[i] + " ";
        }
        if (!currentString.equals("")) {
            result.add(currentString);
        }
        return result.toArray(new String[0]);
    }

    public boolean parseCommand() throws DukeException {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();

        System.out.println(this.divider);

        try {
            switch (Commands.valueOf(splitCommand(command)[0])) {
                case bye:
                    System.out.println("Bye. Hope to see you again soon!");
                    return false;
                case list:
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < this.taskList.size(); i++) {
                        Task currentTask = this.taskList.get(i);
                        System.out.println(i + 1 + "." + currentTask);
                    }
                    System.out.println(this.divider);
                    break;
                case mark:
                    String markMessage = "Nice! I've marked this task as done:\n  ";
                    try {
                        String[] commandList = splitCommand(command);
                        if (commandList.length <= 1) {
                            throw new MarkInvalidException("mark");
                        }
                        int index = Integer.parseInt(commandList[1].replaceAll("\\s", ""));
                        if (index < 1 || index > this.taskList.size()) {
                            throw new MarkInvalidException("mark");
                        }
                        Task currentTask = this.taskList.get(index - 1);
                        currentTask.changeDone();
                        System.out.println(markMessage + currentTask);
                        System.out.println(this.divider);
                        break;
                    } catch (NumberFormatException e) {
                        throw new MarkInvalidException("mark");
                    }
                case unmark:
                    markMessage = "Ok, I've marked this task as not done yet:\n  ";
                    try {
                        String[] commandList = splitCommand(command);
                        if (commandList.length <= 1) {
                            throw new MarkInvalidException("unmark");
                        }
                        int index = Integer.parseInt(commandList[1].replaceAll("\\s", ""));
                        if (index < 1 || index > this.taskList.size()) {
                            throw new MarkInvalidException("unmark");
                        }
                        Task currentTask = this.taskList.get(index - 1);
                        currentTask.changeDone();
                        System.out.println(markMessage + currentTask);
                        System.out.println(this.divider);
                        break;
                    } catch (NumberFormatException e) {
                        throw new MarkInvalidException("unmark");
                    }
                case todo:
                    String[] commandList = splitCommand(command);
                    if (commandList.length <= 1) {
                        throw new MissingTodoException();
                    }
                    Todo currentTodo = new Todo(commandList[1]);
                    this.taskList.add(currentTodo);
                    System.out.println("Got it. I've added this task:\n  " + currentTodo);
                    System.out.println("Now you have " + this.taskList.size() + " tasks in the list.");
                    System.out.println(this.divider);
                    break;
                case deadline:
                    commandList = splitCommand(command);
                    if (commandList.length <= 2) {
                        throw new MissingDeadlineException();
                    }
                    Deadline currentDeadline = new Deadline(commandList[1], commandList[2]);
                    this.taskList.add(currentDeadline);
                    System.out.println("Got it. I've added thi task:\n  " + currentDeadline);
                    System.out.println("Now you have " + this.taskList.size() + " tasks in the list.");
                    System.out.println(this.divider);
                    break;
                case event:
                    commandList = splitCommand(command);
                    if (commandList.length <= 3) {
                        throw new MissingEventException();
                    }
                    Event currentEvent = new Event(commandList[1], commandList[2], commandList[3]);
                    this.taskList.add(currentEvent);
                    System.out.println("Got it. I've added thi task:\n  " + currentEvent);
                    System.out.println("Now you have " + this.taskList.size() + " tasks in the list.");
                    System.out.println(this.divider);
                    break;
                case delete:
                    String deleteMessage = "Noted. I've removed this task:\n  ";
                    try {
                        commandList = splitCommand(command);
                        if (commandList.length <= 1) {
                            throw new DeleteInvalidException();
                        }
                        int deleteIndex = Integer.parseInt(commandList[1].replaceAll("\\s", ""));
                        if (deleteIndex < 1 || deleteIndex > this.taskList.size()) {
                            throw new DeleteInvalidException();
                        }
                        Task deleteTask = this.taskList.get(deleteIndex - 1);
                        this.taskList.remove(deleteTask);
                        System.out.println(deleteMessage + deleteTask);
                        System.out.println(this.divider);
                        break;
                    } catch (NumberFormatException e) {
                        throw new DeleteInvalidException();
                    }
            }
            saveDB();
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException();
        }

        return true;
    }

    public void loadDB() {
        String relativeFilePath = "src/db.txt";

        try {
            Path absolutePath = Paths.get(relativeFilePath).toAbsolutePath();

            // Create the file if it doesn't exist
            if (!Files.exists(absolutePath)) {
                Files.createFile(absolutePath);
                System.out.println("Welcome to theGiantPeach!");
            } else {
                List<String> lines = Files.readAllLines(absolutePath);

                // Process the lines
                for (String line : lines) {
                    String[] words = line.split("\\|");
                    switch (words[0]) {
                        case "T":
                            Todo todo = new Todo(words[2]);
                            if (Objects.equals(words[1], "1")) {
                                todo.changeDone();
                            }
                            taskList.add(todo);
                            break;
                        case "D":
                            Deadline deadline = new Deadline(words[2], words[3]);
                            if (Objects.equals(words[1], "1")) {
                                deadline.changeDone();
                            }
                            taskList.add(deadline);
                            break;
                        case "E":
                            Event event = new Event(words[2], words[3], words[4]);
                            if (Objects.equals(words[1], "1")) {
                                event.changeDone();
                            }
                            taskList.add(event);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDB() {
        List<String> linesToWrite = new ArrayList<>();
        for (int i = 0; i < this.taskList.size(); i++) {
            linesToWrite.add(this.taskList.get(i).toDBString());
        }

        try {
            String relativeFilePath = "src/db.txt";
            Path absolutePath = Paths.get(relativeFilePath).toAbsolutePath();

            Files.write(absolutePath, linesToWrite, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Duke theGiantPeach = new Duke();

        theGiantPeach.loadDB();

        System.out.println(theGiantPeach.divider);
        System.out.println("Hello! I'm TheGiantPeach\nWhat can I do for you?");
        System.out.println(theGiantPeach.divider);

        while (true) {
            try {
                if (!theGiantPeach.parseCommand()) {
                    break;
                }
            } catch (DukeException e) {
                System.out.println(e.getMessage());
                System.out.println(theGiantPeach.divider);
            }
        }

        System.out.println(theGiantPeach.divider);
    }
}
