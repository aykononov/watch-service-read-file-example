/* Приложение мониторит директорию `\watch` и при появлении в ней нового файла `a.txt`, читает его. */

import java.nio.file.*;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class WatchServiceReadFile {
    public static void main(String[] args) {
        String dir = "src/main/java/watch/";
        String file = "a.txt";
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(dir);
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    //System.out.println(path + ": " + event.kind() + ": " + event.context());
                    if (event.kind().toString().equals("ENTRY_CREATE") && event.context().toString().equals(file)) {
                        System.out.println("\nНовый файл...");
                        while (true) {
                            boolean ready = Files.isReadable(Paths.get(dir + file));
                            System.out.println(" готов для чтения: " + ready);
                            if (ready) {
                                try (Scanner scan = new Scanner(Paths.get(dir + file))) {
                                    while (scan.hasNextLine()) {
                                        System.out.println(" читаем строку: " + scan.nextLine());
                                    }
                                } catch (NoSuchElementException e) {
                                    System.out.println(e.getMessage());
                                }
                                System.out.println("Выходим.");
                                break;
                            }
                         }
                    }
                }
                key.reset();
            }
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
/* ---------------------
Новый файл...
 готов для чтения: true
 читаем строку:  1 a
 читаем строку:  2 b
Выходим.

Новый файл...
 готов для чтения: false
 готов для чтения: true
 читаем строку:  1 a
 читаем строку:  2 b
Выходим.

Новый файл...
 готов для чтения: false
 готов для чтения: false
 готов для чтения: false
 готов для чтения: true
 читаем строку:  1 a
 читаем строку:  2 b
Выходим.
 */