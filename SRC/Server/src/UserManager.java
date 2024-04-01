import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class UserManager {
    private Vector<User> users;

    UserManager(){
        users = new Vector<>();
    }

    public Vector<User> getUsers() {
        return users;
    }

    /////////////////////////////////////////////

    public User createUser(String name){
        if (existsUsername(name)){
            return null;
        }

        User newUser = new User(name);
        this.users.add(newUser);
        return newUser;
    }

    public boolean existsUsername(String username){
        for (User user: users){
            if (user.getName().equals(username)){
                return true;
            }
        }
        return false;
    }

    public User getUserByName(String name){
        for (User u: users){
            if (u.getName().equals(name)){
                return u;
            }
        }
        return null;
    }

    public Vector<User> getUsersByString(String usersString){
        String[] usernames = usersString.split("\\|");
        Vector<User> result = new Vector<>();

        for (String username: usernames){
            User u = getUserByName(username);

            if (u == null) {
                return null;
            }

            result.add(u);
        }

        sort(result);

        return result;
    }

    public static void sort(Vector<User> user){
        user.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

}
