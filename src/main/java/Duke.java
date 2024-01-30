import java.nio.file.*;
import java.util.*;

public class Duke {
    Storage storage;
    TaskList taskList;
    Ui ui;

    public Duke(String relativeFilePath) {
        this.ui = new Ui();

        this.storage = new Storage(relativeFilePath);

        try {
            this.taskList = new TaskList(storage.load());
        } catch (LoadStorageException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parseCommand(fullCommand);
                c.execute(taskList, ui, storage);
                isExit = c.isExit();
            } catch (DukeException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }


    public static void main(String[] args) {
        new Duke("src/db.txt").run();
    }
}
