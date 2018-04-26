import com.powder.Exception.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLine {
    private Storage storage;
    private String login;
    private String password;
    private boolean newStorageCreated;
    private final String currentPath = System.getProperty("user.dir");
    private List<String> storageNames;
    private static CommandLine commandLine;

    public static void main(String[] args) throws JSONException, UnknownTypeException, InvalidKeyWordException, IncorrectSyntaxException, BadQueryTypeException {
        Console console = System.console();
        Scanner scanner = new Scanner(System.in);
        init(args);

        if(commandLine.password == null && !commandLine.newStorageCreated){
            char passwordArray[] = console.readPassword("Password: ");
            console.printf("Password entered was: %s%n", new String(passwordArray));
        }

        //check if password is OK

        int counter = 0;
        List<String> command = new ArrayList<>();
        while(true){
            counter++;
            System.out.print(counter + ">");
            String str = scanner.nextLine();
            if(!str.matches("\\s*[Gg][Oo]\\s*")){
                command.add(str);
            }else{
                StringBuilder sB = new StringBuilder();
                for(String s : command){
                    sB.append(s).append(" ");
                }
                JSONObject jsonQuery = new JSONObject(Parser.parseSQLtoJSON(sB.toString()));
                Query query = QueryFactory.getInstance().getQuery(jsonQuery);
                Response response = commandLine.storage.executeQuery(query);
                System.out.println(response.getMessage());
                counter = 0;
                command.clear();
            }
        }
    }

    private static void init(String[] args){
        commandLine = new CommandLine();
        try {
            commandLine.loadListOfStorages();
            for(int i = 0; i < args.length; i += 2){
                commandLine.handleArgument(args[i] + " " + args[i+1]);
            }
        }catch(InvalidArgumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListOfStorages() throws IOException {
        storageNames = Files.readAllLines(Paths.get(currentPath + "/" + "storageNames.txt"), StandardCharsets.UTF_8);
    }

    private void handleArgument(String arg) throws InvalidArgumentException, FileNotFoundException, JSONException {
        Pattern pattern = Pattern.compile("\\s*-(\\S)\\s(\\S+)");
        Matcher matcher = pattern.matcher(arg);
        if(matcher.find() && matcher.matches()){
            switch (matcher.group(1)){
                case "S":
                    loadStorage(matcher.group(2));
                    break;
                case "c":
                    createStorage(matcher.group(2));
                    newStorageCreated = true;
                    break;
                case "U":
                    login = matcher.group(2);
                    break;
                case "P":
                    password = matcher.group(2);
                    break;
                case "q":
                    inlineCommand(matcher.group(2));
                    break;
                case "Q":
                    inlineCommand(matcher.group(2));
                    System.exit(0);
                case "d":
                    useDatabase(matcher.group(2));
                    break;

            }
        }else{
            throw new InvalidArgumentException();
        }
    }

    private void useDatabase(String databaseName) {
    }

    private void inlineCommand(String command) {
    }

    private void createStorage(String storageName) throws JSONException {
        File file = new File(currentPath + "/Storages");
        if(file.exists()){
            file.mkdir();
        }

        Storage storage = new Storage(storageName);
        storage.saveToFile();
    }

    private void loadStorage(String _storageName) throws FileNotFoundException, JSONException {
        for(String storageName : storageNames){
            if(storageName.equalsIgnoreCase(_storageName)){
                File file = new File(currentPath + "/" + "Storages" + "/" + storageName + ".json");
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\\Z");
                JSONObject jsonStorage = new JSONObject(scanner.next());
                storage = new Storage(jsonStorage);
            }
        }
    }
}
