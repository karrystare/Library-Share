package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Library {

    /**
     * Require the ArrayList Object to implement the interface CustomObject 
     * to specify which variable to be compared. <p>
     * Can Detect Null ArrayList
     * @param <T>  Any Object Class that has implemented CustomObject interface
     * @param list  Consist of Object that satisfied T
     * @param testVariable  Variable Needed To Find
     * @return indexID - Index Of Object With Such Variable <p> 
     * Return -1 If Not Found or The List is Empty or Null
     */
    public static <T extends CustomObject> int find(ArrayList<T> list, String testVariable){
        if(!isEmpty(list)){
            for(int i = 0; i < list.size(); i++)
                if(list.get(i).getVariable().equals(testVariable))
                    return i;            
        }
        return -1;
    }
    
    /**
     * Improved isEmpty method to check null
     * @param collection  Object that extended from Collection
     * @return  True if the collection is null or empty 
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }    
    
    public static String input(String message, boolean allowEmpty){
        Scanner sc = new Scanner(System.in);
        String input;
        while(true){
            System.out.print(message);          
            if(sc.hasNextLine()){
                input = sc.nextLine();
                if(input != null && (allowEmpty || (!input.isEmpty() && !allowEmpty)))
                    return input;
                System.out.println("Cannot Input Nothing");
            }
            else
                System.out.println("Unsupported Input Detected");
        }          
    }  

    public static double inputDouble(String message, double... limit){
        while(true){
            try{
                double num = Double.parseDouble(input(message, false).trim());
                switch(limit.length){
                    case 0 -> {
                        return num;
                    }
                    case 2 -> {
                        if(limit[0] > limit[1]){
                            double temp = limit[0];
                            limit[0] = limit[1];
                            limit[1] = temp;
                        }
                        if(num >= limit[0] && num <= limit[1])
                            return num;
                        else
                            System.out.println("Number Entered Was Not In Range");
                    }
                    default -> {
                        System.out.println("Invalid Limit");
                        return num;
                    }       
                }
            }catch(NumberFormatException e){
                System.out.println(message + " must be an integer.");
            }
        }        
    }
      
    public static String capitalizeString(String str){
        if(str.isBlank())
            return str;
        str = Arrays.stream(str.toLowerCase().trim().split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));        
        return str;
    } 
    
    public static byte[] getByte(String password){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedPassword = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
            return encodedPassword;
        } catch (NoSuchAlgorithmException ex) {
            System.out.print("");
        }	
        return null;
    }
    
    public static String hashPassword(String password){
        StringBuilder hexString = new StringBuilder();
        byte[] hash = getByte(password);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();    
    }    
    
    public static boolean findPart(String str, String regex, boolean isCaseSensitive){
        Pattern format;
        if(isCaseSensitive)
            format = Pattern.compile(regex);
        else
            format = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);       
        Matcher stringMatcher = format.matcher(str);
        return stringMatcher.find();
    }    
    
    public static boolean validateDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);       
        try{
            format.parse(date);
            return true;
        }catch(ParseException e){
            return false;
        }              
    } 
    
    public static boolean continueSwitch(String message){
        while(true){
           switch(input(message, false)){
               case "Y" -> {
                   return true;
                }
               case "N" -> {        
                   return false;
                }
               default -> System.out.println("Invalid Input");            }        
        }
    }   
    
    public static String[] updateArray(String[] currentInfo, String[] newInfo){
        for(int i = 0; i < currentInfo.length; i++)
            if(newInfo[i] != null && (!newInfo[i].isEmpty()))
                currentInfo[i] = newInfo[i];
        return currentInfo;
    }    
    
    public static boolean writeTxt(String filePath, String infoOut){
        Path path = Paths.get(filePath);
        try {
            Files.writeString(path, infoOut, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException ex) {
            System.out.println("File Is Inaccessible");
        } 
        return false;
    }
    
    public static ArrayList<String> readTxt(String filePath){
        ArrayList<String> userInfoList = new ArrayList<>();
        Path path = Paths.get(filePath);
        try{
            String info = Files.readString(path);
            if(findPart(info, "\n", false))
                userInfoList.addAll(Arrays.asList(info.split("\n")));
            return userInfoList;
        }catch(IOException e){
            System.out.println("File Inaccessible or There was nothing to read");
            return null;
        }
    }

    public static String inputUsername(){
        while(true){
            String username = input("Enter username (Minimum 5 Characters): ", false);
            String regex = "^(?=.{5,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
            if(findPart(username, regex, false))
                return username;
            System.out.println("Invalid Username "
                    + "(Must not contain space and special characters)");
        }
    }
    
    public static String inputPassword(boolean reConfirm){
        while(true){
            String password = input("Enter Password: ", false);
            String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
            String negate ="[\\\\;]";
            
            if(!reConfirm)
                return hashPassword(password);  
            
            if(findPart(password, regex, false) && (!findPart(password, negate, false))){                
                String passwordConfirm;
                do{
                    passwordConfirm = input("Confirm Password (Enter 0 to reset): ", false);
                    if(password.equals(passwordConfirm))
                        return hashPassword(password);
                    else if(!passwordConfirm.equals("0"))
                        System.out.println("Passwords Mismatched");                    
                }while(!passwordConfirm.equals("0"));
            }
            else{
                System.out.println("\nInvalid Format, A Password Must Have");
                System.out.println("At Least 1 upper case letter");
                System.out.println("At least 1 lower case letter");
                System.out.println("At least 1 number");
                System.out.println("No white space");
                System.out.println("At least 6 characters total\n");
            }     
        }
    }
    
    public static String inputName(String type, boolean allowEmpty){
        while(true){
            String name = input("Enter " + type + " Name: ", allowEmpty);
            String regex = "^[A-Za-z0-9 '.]+$";
            if((allowEmpty && name.isEmpty()) || (!name.isEmpty() && findPart(name, regex, false)))
                return capitalizeString(name);
            System.out.println("Invalid Name");
        }           
    }
    
    public static String inputPhoneNumber(boolean allowEmpty){
        while(true){
            String phone = input("Enter Phone Number (10 Digits): ", allowEmpty);
            String regex = "^[0-9]{10}$";
            if(findPart(phone, regex, false) || (allowEmpty && phone.isEmpty()))
                return phone;
            System.out.println("Invalid Phone Number");
        }         
    }
    
    public static String inputEmail(boolean allowEmpty){
        while(true){
            String email = input("Enter Email: ", allowEmpty);
            String regex = "^\\w+[A-Za-z0-9._%+-]?+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            if(findPart(email, regex, false) || (allowEmpty && email.isEmpty()))
                return email;
            System.out.println("Invalid Email");
        }       
    }   
    
    public void findFromTxt(){
        String key = input("Enter Search Key: ", false);
        String path = input("Enter File Path: ", false);
        ArrayList<String> infoList = readTxt(path);
        
        if(isEmpty(infoList)){
            return;
        }
                 
        Optional<String> check = infoList.stream()
                .filter(infoString -> infoString.substring(0, infoString.indexOf(";")).equals(key))
                .findFirst();
          
        System.out.println(check.isPresent() ? "Exist" : "Does not exist");
    }    
}
