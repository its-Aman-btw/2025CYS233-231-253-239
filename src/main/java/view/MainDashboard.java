package view;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import util.DBConnection;
import util.ReportManager;
import java.sql.*;
import java.time.LocalDate;

public class MainDashboard extends Application {

    private Stage primaryStage;
    private StackPane contentArea = new StackPane();
    private VBox panelMembers, panelStaff, panelReports;

    // UI Components
    private TextField txtFirstName = new TextField(), txtLastName = new TextField(), txtEmail = new TextField(), txtPhone = new TextField();
    private DatePicker dpDobMember = new DatePicker();
    private ComboBox<String> cmbGenderMember = new ComboBox<>();
    private TextField txtUsername = new TextField(), txtDeleteUser = new TextField();
    private PasswordField txtPassword = new PasswordField(), txtNewPassword = new PasswordField();
    private TextField txtForgotPasswordUsername = new TextField();

    private static final String PRIMARY_COLOR = "#6200EE";
    private static final String BACKGROUND_COLOR = "#121212";
    private static final String SURFACE_COLOR = "#1E1E1E";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox box = new VBox(20); box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        TextField u = new TextField(); u.setMaxWidth(250); u.setPromptText("Username");
        PasswordField p = new PasswordField(); p.setMaxWidth(250); p.setPromptText("Password");
        Button btn = new Button("LOGIN"); btn.setPrefWidth(250);
        btn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        btn.setOnAction(e -> {
            if(handleLogin(u.getText(), p.getText())) {
                showAlert("Success", "Welcome, " + u.getText() + "!");
                showDashboard();
            } else {
                showAlert("Error", "Invalid Login");
            }
        });
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        forgotPasswordLink.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        forgotPasswordLink.setOnAction(e -> showForgotPasswordScreen());
        box.getChildren().addAll(new Label("IRON PULSE LOGIN") {{ setStyle("-fx-text-fill:white; -fx-font-size:24;"); }}, u, p, btn, forgotPasswordLink);
        primaryStage.setScene(new Scene(box, 400, 500));
        primaryStage.show();
    }

    private void showDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        VBox sidebar = new VBox(20); sidebar.setPadding(new Insets(20)); sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SURFACE_COLOR + ";");

        Button b1 = new Button("👥 Athletes"); b1.setMaxWidth(Double.MAX_VALUE);
        Button b2 = new Button("🔐 Admin Mgmt"); b2.setMaxWidth(Double.MAX_VALUE);
        Button b3 = new Button("📋 Analytics"); b3.setMaxWidth(Double.MAX_VALUE);

        b1.setOnAction(e -> showPanel(panelMembers));
        b2.setOnAction(e -> showPanel(panelStaff));
        b3.setOnAction(e -> showPanel(panelReports));

        sidebar.getChildren().addAll(new Label("IRON PULSE") {{ setStyle("-fx-text-fill:"+PRIMARY_COLOR+"; -fx-font-size:20;"); }}, b1, b2, b3);

        panelMembers = createMembersPanel();
        panelStaff = createStaffPanel();
        panelReports = createReportsPanel();

        contentArea.getChildren().addAll(panelMembers, panelStaff, panelReports);
        root.setLeft(sidebar); root.setCenter(contentArea);
        primaryStage.setScene(new Scene(root, 1200, 700));
        showPanel(panelMembers);
    }

    private VBox createMembersPanel() {
        VBox vb = new VBox(20); vb.setPadding(new Insets(30));
        GridPane g = new GridPane(); g.setHgap(15); g.setVgap(15);
        g.add(new Label("First Name:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 0); g.add(txtFirstName, 1, 0);
        g.add(new Label("Last Name:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 1); g.add(txtLastName, 1, 1);
        g.add(new Label("Email:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 2); g.add(txtEmail, 1, 2);
        g.add(new Label("Phone:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 3); g.add(txtPhone, 1, 3);
        g.add(new Label("Date of Birth:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 4); g.add(dpDobMember, 1, 4);
        g.add(new Label("Gender:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 5); g.add(cmbGenderMember, 1, 5);
        Button b = new Button("Register"); b.setOnAction(e -> handleMemberSave());
        g.add(b, 1, 6);

        // Member Delete Section
        g.add(new Label("Delete Member:") {{ setStyle("-fx-text-fill:red;"); }}, 0, 8);
        TextField txtDeleteMember = new TextField();
        g.add(txtDeleteMember, 1, 8);
        Button btnDeleteMember = new Button("Delete Member");
        btnDeleteMember.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        btnDeleteMember.setOnAction(e -> deleteMember(txtDeleteMember.getText()));
        g.add(btnDeleteMember, 1, 9);

        cmbGenderMember.getItems().addAll("Male", "Female", "Other");
        vb.getChildren().addAll(new Label("REGISTER ATHLETE") {{ setStyle("-fx-text-fill:white;"); }}, g);
        return vb;
    }

    private VBox createStaffPanel() {
        VBox vb = new VBox(20); vb.setPadding(new Insets(30));
        GridPane g = new GridPane(); g.setHgap(15); g.setVgap(15);
        g.add(new Label("User:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 0); g.add(txtUsername, 1, 0);
        g.add(new Label("Pass:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 1); g.add(txtPassword, 1, 1);
        Button btnS = new Button("Create Admin"); btnS.setOnAction(e -> handleUserSave());
        g.add(btnS, 1, 2);

        // Modernize Admin Management - Add Change Password
        g.add(new Label("New Pass:") {{ setStyle("-fx-text-fill:white;"); }}, 0, 3); g.add(txtNewPassword, 1, 3);
        Button btnChangePass = new Button("Change Password");
        btnChangePass.setOnAction(e -> handleChangePassword(txtUsername.getText(), txtNewPassword.getText()));
        g.add(btnChangePass, 1, 4);

        g.add(new Label("Delete User:") {{ setStyle("-fx-text-fill:red;"); }}, 0, 5); g.add(txtDeleteUser, 1, 5);
        Button btnD = new Button("Delete Admin"); btnD.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        btnD.setOnAction(e -> deleteAdmin(txtDeleteUser.getText()));
        g.add(btnD, 1, 6);
        vb.getChildren().addAll(new Label("ADMIN MANAGEMENT") {{ setStyle("-fx-text-fill:white;"); }}, g);
        return vb;
    }

    private VBox createReportsPanel() {
        VBox vb = new VBox(20); vb.setPadding(new Insets(30));
        FlowPane fp = new FlowPane(15, 15);
        String[][] reports = {
                {"1. Active", "SELECT * FROM vw_ActiveMemberships", "R1.pdf"}, {"2. Schedule", "SELECT * FROM vw_ClassScheduleDetails", "R2.pdf"},
                {"3. Revenue", "SELECT * FROM vw_MonthlyRevenue", "R3.pdf"}, {"4. Equipment", "SELECT * FROM vw_EquipmentStatus", "R4.pdf"},
                {"5. Trainer Load", "SELECT * FROM vw_TrainerWorkload", "R5.pdf"}, {"6. All Members", "SELECT * FROM Members", "R6.pdf"},
                {"7. Payments", "SELECT * FROM Payments", "R7.pdf"}, {"8. Trainers", "SELECT * FROM Trainers", "R8.pdf"},
                {"9. Classes", "SELECT * FROM Classes", "R9.pdf"}, {"10. Audit", "SELECT * FROM SystemAudit", "R10.pdf"}
        };
        for(String[] r : reports) {
            Button btn = new Button(r[0]);
            btn.setOnAction(e -> {
                ReportManager.generateBusinessReport(r[0], r[1], r[2]);
                showAlert("Success", r[0] + " Report Generated Successfully!");
            });
            fp.getChildren().add(btn);
        }
        vb.getChildren().addAll(new Label("10 SYSTEM REPORTS") {{ setStyle("-fx-text-fill:white;"); }}, fp);
        return vb;
    }

    // Backend Helpers
    private boolean handleLogin(String u, String p) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Users WHERE username=? AND password=?")) {
            ps.setString(1, u); ps.setString(2, p); return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private void showForgotPasswordScreen() {
        Stage forgotStage = new Stage();
        forgotStage.setTitle("Forgot Password");
        forgotStage.initModality(Modality.APPLICATION_MODAL);
        forgotStage.initOwner(primaryStage);

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        Label title = new Label("Forgot Password");
        title.setStyle("-fx-text-fill:white; -fx-font-size:20;");

        txtForgotPasswordUsername.setMaxWidth(250);
        txtForgotPasswordUsername.setPromptText("Username");

        Button btnReset = new Button("Reset Password");
        btnReset.setPrefWidth(250);
        btnReset.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        btnReset.setOnAction(e -> handleForgotPassword(txtForgotPasswordUsername.getText()));

        box.getChildren().addAll(title, txtForgotPasswordUsername, btnReset);

        Scene scene = new Scene(box, 350, 250);
        forgotStage.setScene(scene);
        forgotStage.showAndWait();
    }

    private void handleForgotPassword(String username) {
        // In a real application, this would send a reset link or temporary password via email.
        // For this example, we'll just show an alert.
        showAlert("Info", "If " + username + " is registered, a password reset link has been sent.");
    }

    private void handleChangePassword(String username, String newPassword) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Users SET password=?, password_hash=? WHERE username=?")) {
            ps.setString(1, newPassword);
            ps.setString(2, newPassword); // In a real app, hash this password
            ps.setString(3, username);
            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                showAlert("Success", "Password for " + username + " changed successfully!");
            } else {
                showAlert("Error", "User not found or password not changed.");
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void deleteMember(String memberIdentifier) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM Members WHERE first_name=? OR last_name=? OR email=?")) {
            ps.setString(1, memberIdentifier);
            ps.setString(2, memberIdentifier);
            ps.setString(3, memberIdentifier);
            if(ps.executeUpdate() > 0) showAlert("Success", "Member Deleted!"); else showAlert("Error", "Member Not Found");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void handleMemberSave() {
        try (Connection c = DBConnection.getConnection(); CallableStatement cs = c.prepareCall("{call sp_RegisterMember(?, ?, ?, ?, ?)}")) {
            // Combining First and Last name to fit the 5-parameter procedure
            String fullName = txtFirstName.getText() + " " + txtLastName.getText();
            cs.setString(1, fullName.trim());
            cs.setString(2, txtEmail.getText());
            cs.setString(3, txtPhone.getText());
            cs.setDate(4, Date.valueOf(dpDobMember.getValue()));
            cs.setString(5, cmbGenderMember.getValue());
            cs.execute();
            showAlert("Success", "Registered!");
        } catch (SQLException e) { showAlert("Error", e.getMessage()); }
    }

    private void handleUserSave() {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Users (username, password, password_hash, role) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, txtUsername.getText()); ps.setString(2, txtPassword.getText());
            ps.setString(3, txtPassword.getText()); ps.setString(4, "Admin");
            ps.executeUpdate(); showAlert("Success", "User Created!");
        } catch (SQLException e) { showAlert("Error", e.getMessage()); }
    }

    private void deleteAdmin(String u) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM Users WHERE username=?")) {
            ps.setString(1, u);
            if(ps.executeUpdate() > 0) showAlert("Success", "Deleted!"); else showAlert("Error", "Not Found");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void showPanel(VBox p) { panelMembers.setVisible(p==panelMembers); panelStaff.setVisible(p==panelStaff); panelReports.setVisible(p==panelReports); }
    private void showAlert(String t, String m) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setContentText(m); a.show(); }
    public static void main(String[] args) { launch(args); }
}
    