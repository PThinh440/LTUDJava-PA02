import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

class ClientConnection extends Thread{
    private final Socket clientSocket;
    private User user;
    private final BufferedWriter sender;
    private final BufferedReader receiver;

    ClientConnection(Socket clientSocket){
        this.clientSocket = clientSocket;
        this.user = null;

        try {
            this.sender = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()));
            this.receiver = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public User getUser(){
        return user;
    }

    /////////////////////////////////////////////

    private void requestHandler() throws IOException {

        String req = receiver.readLine();
        System.out.println("Received: " + req);
        String[] pattern = req.split("`");

        if (pattern.length < 2){
            return;
        }

        switch (pattern[0]){
            case "Sign up": {
                user = Main.getChatServer().getUserManager().createUser(pattern[1]);

                if (user != null) {
                    sendMsgToUser("Verified`" + pattern[1]);

                    // Update database and send notify to online users
                    Main.getChatServer().updateChatDatabaseWithNewUser(user);
                    Main.getChatServer().broadcastNewUser(user);

                    System.out.println("This thread's account is: " + user.getName());
                } else{
                    sendMsgToUser("Error`This username is already existed.");
                }
                break;
            }
            case "Sign in": {
                user = Main.getChatServer().getUserManager().getUserByName(pattern[1]);

                if (user == null){
                    sendMsgToUser("Error`This username does not existed.");
                } else{
                    sendMsgToUser("Access granted`" + pattern[1]);

                    System.out.println("This thread's account is: " + user.getName());

                }

                break;
            }
            case "Sign out": {
                System.out.println(user.getName() + " signed out");
                sendMsgToUser("Signed out`");

                user = null;
                break;
            }
            case "New group chat": {
                // New group chat`userA|userB|userC
                Vector<User> participants = Main.getChatServer().getUserManager().getUsersByString(pattern[1]);

                if (participants == null){
                    // Send error
                    sendMsgToUser("Error`There is no such user.");
                } else {
                    ChatBox cb = Main.getChatServer().getChatBoxManager().createNewChat(participants);
                    Main.getChatServer().broadcastNewGroupChat(cb);
                }
                break;
            }
            case "Get chats": {
                String msg = "Chat list" + user.getUserChatListString();

                sendMsgToUser(msg);

                break;
            }
            case "Get file": {
                String path = pattern[2].replace(": ", "\\")
                        .replace("[", "").replace("]", "");
                path = "files\\" + path;
                File file = new File(path);

                System.out.println(path);

                sendMsgToUser("File`" + file.getName() + "`" + file.length());
                sendFile(file);

                break;
            }
            case "Send": case "Delete": case "File": {
                // Send`userA|userB`sender: msg
                // Delete`userA|userB`sender: msg
                // File`userA|userB`sender: [File]`Size

                // Get chat box for updating
                // Auto sorted
                ChatBox cb = Main.getChatServer().getChatBoxManager()
                        .getChat(Main.getChatServer().getUserManager().getUsersByString(pattern[1]));

                if (cb != null){
                    // Updating
                    switch(pattern[0]){
                        case "Send":
                            cb.getMessages().add(pattern[2]);
                            break;
                        case "Delete":
                            int index = cb.getMessages().indexOf(pattern[2]);
                            cb.getMessages().set(index, "//Removed message//");
                            break;
                        case "File": {
                            // Get file name
                            // "sender: [File]" -> "[File]" -> "File"
                            String fileName = pattern[2]
                                    .substring(pattern[2].indexOf('['));
                            fileName = fileName.substring(1, fileName.length() - 1);

                            // Get file size
                            int fileSize = Integer.parseInt(pattern[3]);

                            System.out.println(fileName + ", " + fileSize);
                            saveFileUploadedByUser(fileName, fileSize);

                            sendMsgToUser("Notify`Uploading success!");
                            cb.getMessages().add(pattern[2]);
                            break;
                        }
                    }

                    // Send update signal to that chat box's users
                    Main.getChatServer().sendMultipleChatUpdates(cb);

                } else {
                    System.out.println("Cannot find chat: " + pattern[1]);
                }
                break;
            }
            default:
                break;
        }
    }

    public void sendMsgToUser(String msg){
        try {
            sender.write(msg);
            sender.newLine();
            sender.flush();
            System.out.println("Sent msg: " + msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFile(File file) {
        try {
            // Read file into bytes and send
            InputStream in = new FileInputStream(file);
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(in.readAllBytes());
            outputStream.flush();

            in.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot send file");
        }
    }

    public void saveFileUploadedByUser(String fileName, int fileSize){
        // Create folder to store upload files
        String mainPath = System.getProperty("user.dir")  + "\\files";

        File folder = new File(mainPath);
        folder.mkdir();

        String userPath = mainPath+ "\\" + user.getName();
        folder = new File(userPath);
        folder.mkdir();

        try {
            File file = new File(userPath + "\\" + fileName);

            if(!file.exists()) {
                file.createNewFile();
            }
            InputStream in = clientSocket.getInputStream();
            OutputStream os = new FileOutputStream(file);

            System.out.println("Reading");
            os.write(in.readNBytes(fileSize));
            System.out.println("Read");
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        try {
            System.out.println("Talking to client");
            System.out.println(clientSocket.getPort());

            while(true){
                System.out.println("Waiting for client...");
                requestHandler();
            }

            // check exist user -> make account
        }
        catch(IOException e){
            System.out.println("Client disconnected");

        }
    }
}