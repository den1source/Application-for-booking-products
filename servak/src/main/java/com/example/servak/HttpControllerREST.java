package com.example.servak;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

@RestController
public class HttpControllerREST extends HttpServlet {

    @RequestMapping("/download_image")
    public String uploadImage(@RequestBody byte[] imageBytes, HttpServletRequest request) throws SQLException, ClassNotFoundException, IOException {
        //bytesToImage(imageBytes, "Data/Images/Types/");
        int index = 0;
        ArrayList<Integer> arr = get_all_types_id();
        do {
            if (arr.contains(index)) {
                index++;
            } else break;
        } while (true);
        bytesToImage(imageBytes, "Data/Images/Types/" + index + ".jpg");
        //System.out.println(request.getParameter("name")+"!!!!!!!!!!!!!!!!!!!!!!!!");
        return String.valueOf(add_type(index, request.getParameter("name")));
    }

    private boolean add_type(int index, String name) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            PreparedStatement pr_stmt = null;
            String sql = "INSERT INTO types_of_products (ID,VID) VALUES (?,?)";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setLong(1, index);
            pr_stmt.setString(2, name);
            pr_stmt.executeUpdate();
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return true;
    }

    @RequestMapping("/type")
    public boolean check_data(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_name":
                return get_all_types().contains(request.getParameter("name"));
            case "delete_type":
                String[] numbersArray = request.getParameter("str").split(",");
                ArrayList<Integer> numbersList = new ArrayList<>();
                for (String number : numbersArray) {
                    int num = Integer.parseInt(number);
                    numbersList.add(num);
                }
                System.out.println(numbersList);
                deleteTypes(numbersList);
                for (int i : numbersList) {
                    delete_file(i + ".jpg");
                }
                return true;
        }
        return false;
    }

    private void delete_products_of_type(int id) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM types_of_products WHERE ID = ?";
            statement = connection.prepareStatement(query);

            for (int id : ids) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    private boolean delete_file(String imageName) {
        String directoryPath = "Data/Images/Types/"; // Путь к директории с изображениями
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(imageName)) {
                        return file.delete();
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImage(HttpServletRequest request) throws IOException {
        switch (request.getParameter("what_do")) {
            case "images_menu" -> {//главное меню
                byte[] imageBytes = imageToBytes("Data/Images/Types/" + request.getParameter("number") + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }

            case "images_products" -> {//выбор товара
                byte[] imageBytes = imageToBytes("Data/Images/Products/" + request.getParameter("number") + ".jpg");
                System.out.println("Data/Images/Products/" + request.getParameter("number") + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }
            default -> {
                byte[] imageBytes = imageToBytes("Data/Images/Errors/" + 0 + ".jpg");
                return ResponseEntity.ok().body(imageBytes);
            }
        }
    }

    public static byte[] imageToBytes(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static void bytesToImage(byte[] imageBytes, String outputPath) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        File outputFile = new File(outputPath);
        ImageIO.write(bufferedImage, "jpg", outputFile);
    }

    public ArrayList<Integer> get_all_types_id() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(rs.getInt("id"));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<String> get_all_types() throws ClassNotFoundException, SQLException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(rs.getString("vid"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    public ArrayList<String> get_all_from_types() throws ClassNotFoundException, SQLException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM types_of_products");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("vid"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;
    }

    @RequestMapping("/data")
    public ResponseEntity<String> post_data(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "number_of_products":
                ArrayList<String> products = get_num_of_products(request.getParameter("num"));
                return ResponseEntity.ok().body(new Gson().toJson(products));
            case "get_id":
                ArrayList<String> id = get_id_of_products(request.getParameter("num"));
                return ResponseEntity.ok().body(new Gson().toJson(id));
            case "gettypes":
                ArrayList<String> types = get_all_from_types();
                return ResponseEntity.ok().body(new Gson().toJson(types));
            default:
                ArrayList<String> arr = new ArrayList<>();
                arr.add("0");
                return ResponseEntity.ok().body(new Gson().toJson(arr));
        }
    }

    private void deleteTypes(ArrayList<Integer> ids) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                    "denis21042", "JfWRQ5PG9iKn");
            String query = "DELETE FROM types_of_products WHERE ID = ?";
            statement = connection.prepareStatement(query);

            for (int id : ids) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }


    @RequestMapping("/auth")
    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_and_add_data" -> {
                boolean cont_ = contains_phone_in_BD(request.getParameter("phone"));
                if (cont_ == true) {
                    return "already";
                } else {
                    add_data(request.getParameter("name"), (request.getParameter("phone")), request.getParameter("pass"));
                    return "good";
                }
            }
            case "enter_chk" -> {
                //System.out.println(request.getParameter("phone")+" "+ request.getParameter("pass"));
                return check_pass_log_user(request.getParameter("phone"), request.getParameter("pass"), "table_of_users");
            }
            case "admin_chk" -> {
                System.out.println(request.getParameter("phone") + " " + request.getParameter("pass"));
                return check_pass_log_admin(request.getParameter("phone"), request.getParameter("pass"), "table_of_admins");
            }
            case "number_of_types" -> {
                return get_numbers_lines("types_of_products", "vid");
            }
            default -> {
                return "error";
            }
        }
    }

    private ArrayList<String> get_num_of_products(String str) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products WHERE vid='" + Integer.parseInt(str) + "'");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("type"));
                data.add(String.valueOf(rs.getFloat("price")));
                data.add(String.valueOf(rs.getInt("time")));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;

    }

    private ArrayList<String> get_id_of_products(String str) throws SQLException, ClassNotFoundException {
        ArrayList<String> data = new ArrayList<>();

        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM table_of_products WHERE id='" + Integer.parseInt(str) + "'");
            while (rs.next()) {
                data.add(String.valueOf(rs.getInt("id")));
                data.add(rs.getString("type"));
                data.add(String.valueOf(rs.getFloat("price")));
                data.add(String.valueOf(rs.getInt("time")));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println(data);
        return data;

    }

    private String get_numbers_lines(String t_n, String name_column) throws ClassNotFoundException, SQLException {
        int c1 = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                String tt = rs.getString(name_column);
                if (!arrayList.contains(tt)) arrayList.add(tt);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return String.valueOf(arrayList.size());
    }

    public void add_data(String name, String phone, String password) {
        Thread thread = new Thread(() -> {
            try {
                long id = 0;
                ArrayList<Long> all_id = get_all_id("table_of_users");
                if (all_id.size() != 0) {
                    id--;
                    do {
                        id++;
                    } while (all_id.contains(id));
                }
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                        "denis21042", "JfWRQ5PG9iKn");
                try {
                    PreparedStatement pr_stmt = null;
                    String sql = "INSERT INTO table_of_users (ID,NAME_USER,PHONE,PASSWORD_OF_USER) VALUES (?,?,?,?)";
                    pr_stmt = c.prepareStatement(sql);
                    pr_stmt.setLong(1, id);
                    pr_stmt.setString(2, name);
                    pr_stmt.setString(3, phone);
                    pr_stmt.setString(4, password);
                    pr_stmt.executeUpdate();
                    pr_stmt.close();
                    c.close();
                } catch (Exception e) {
                    System.out.println("Ошибка во внесении данных в БД");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public boolean contains_phone_in_BD(String phone) {
        try {
            return get_all_phones().contains(phone);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String check_pass_log_admin(String login, String pass, String table) {
        try {
            System.out.println(get_all_logins().contains(login) + " " + get_all_password(table).contains(pass));
            if (get_all_logins().contains(login) && get_all_password(table).contains(pass)) {
                return "ok";
            } else if (!get_all_logins().contains(login) && get_all_password(table).contains(pass)) {
                return "ph_no";
            } else if (get_all_logins().contains(login) && !get_all_password(table).contains(pass)) {
                return "pass_no";
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("11111");
            return "error";
        }
        return "error";
    }

    public String check_pass_log_user(String phone, String pass, String table) {
        try {
            //return get_all_phones().contains(phone) && get_all_password(table).contains(pass);
            System.out.println(get_all_phones().contains(phone) + " " + get_all_password(table).contains(pass));
            if (get_all_phones().contains(phone) && get_all_password(table).contains(pass)) {
                return "ok";
            } else if (!get_all_phones().contains(phone) && get_all_password(table).contains(pass)) {
                return "ph_no";
            } else if (get_all_phones().contains(phone) && !get_all_password(table).contains(pass)) {
                return "pass_no";
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "error";
        }
        return "error";
    }

    private ArrayList<String> get_all_password(String t_n) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> pass_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                String login = rs.getString("PASSWORD_OF_USER");
                pass_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return pass_tt;
    }

    private ArrayList<String> get_all_logins() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "table_of_admins");
            while (rs.next()) {
                String login = rs.getString("LOGIN_OF_USER");
                login_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return login_tt;
    }

    private ArrayList<String> get_all_phones() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + "table_of_users");
            while (rs.next()) {
                String login = rs.getString("PHONE");
                login_tt.add(login);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return login_tt;
    }

    private ArrayList<Long> get_all_id(String t_n) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<Long> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
            while (rs.next()) {
                Long id = rs.getLong("ID");
                id_tt.add(id);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return id_tt;
    }


}

