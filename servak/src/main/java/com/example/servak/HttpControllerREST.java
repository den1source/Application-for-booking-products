package com.example.servak;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

@RestController
public class HttpControllerREST extends HttpServlet {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestBody byte[] imageBytes) {
        // Здесь выполняется логика загрузки изображения
        // imageBytes - массив байтов, представляющий загруженное изображение

        // Возвращаем сообщение об успешной загрузке
        return new ResponseEntity<>("Image uploaded successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImage(HttpServletRequest request) throws IOException {
        String str=request.getParameter("number");
        byte[] imageBytes = imageToBytes("Data/Images/Types/"+"image.jpeg");
        return ResponseEntity.ok().body(imageBytes);
    }

    public static byte[] imageToBytes(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }







    @RequestMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException {
        switch (request.getParameter("what_do")) {
            case "check_and_add_data" -> {
                boolean cont_ = contains_login_in_BD(request.getParameter("login"));
                if (cont_ == true) {
                    return "already";
                } else {
                    add_data(request.getParameter("name"), request.getParameter("lastname"), Integer.parseInt(request.getParameter("year")), request.getParameter("login"), request.getParameter("pass"));
                    return "good";
                }
            }
            case "enter_chk" -> {
                boolean cont = check_pass_log(request.getParameter("login"), request.getParameter("pass"), "table_of_users");
                if (cont) {
                    return "go";
                } else return "no";
            }
            case "admin_chk" -> {
                boolean cont = check_pass_log(request.getParameter("login"), request.getParameter("pass"), "table_of_admins");
                if (cont) {
                    return "go";
                } else return "no";
            }
            case "number_of_types" -> {
                return get_numbers_lines("types_of_products", "vid");
            }

            default -> {
                return "error";
            }
        }
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

    public void add_data(String name, String last_name, int year, String login, String password) {
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
                    String sql = "INSERT INTO table_of_users (ID,NAME_USER,LAST_NAME_USER,YEAR_OF_USER,LOGIN_OF_USER,PASSWORD_OF_USER) VALUES (?,?,?,?,?,?)";
                    pr_stmt = c.prepareStatement(sql);
                    pr_stmt.setLong(1, id);
                    pr_stmt.setString(2, name);
                    pr_stmt.setString(3, last_name);
                    pr_stmt.setInt(4, year);
                    pr_stmt.setString(5, login);
                    pr_stmt.setString(6, password);
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

    public boolean contains_login_in_BD(String login) {
        try {
            return get_all_logins("table_of_users").contains(login);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean check_pass_log(String pass, String login, String table) {
        try {
            return get_all_logins(table).contains(login) && get_all_password(table).contains(pass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    private ArrayList<String> get_all_logins(String t_n) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection("jdbc:postgresql://ep-shiny-recipe-198866.eu-central-1.aws.neon.tech/neondb",
                "denis21042", "JfWRQ5PG9iKn");
        ArrayList<String> login_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + t_n);
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

