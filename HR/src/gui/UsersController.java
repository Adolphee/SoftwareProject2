package gui;

import java.io.IOException;
import database.LogDAO;
import database.PersoneelDAO;
import database.UserDAO;
import database.WebUserDAO;
import email.Email;
import exceptions.UserOnbestaandException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import logic.Personeel;
import logic.User;

public class UsersController {

	// Applicatie
	@FXML
	Tab applicatie;
	@FXML
	TableView<User> aUsers;
	@FXML
	TableColumn<User, String> acEmail;
	@FXML
	TableColumn<User, String> acNaam;
	@FXML
	TableColumn<User, String> acAdmin;
	@FXML
	Button bAddUser;
	@FXML
	Button bRemAdmin;
	@FXML
	Button bSetAdmin;
	@FXML
	Button abPassword;
	@FXML
	Label alSelectie;

	@FXML
	private void handleAppUser() throws IOException {
		alSelectie.setText("");
		Stage popup = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("AddAppUser.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		popup.setTitle("User toevoegen");
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setResizable(false);
		popup.centerOnScreen();
		popup.setScene(scene);
		popup.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				initAppUsers();
			}
		});
		popup.show();
	}

	@FXML
	private void handleRemAdmin() {
		if (aUsers.getSelectionModel().getSelectedItems().size() == 0) {
			alSelectie.setStyle("-fx-text-fill: red");
			alSelectie.setText("Geen user geselecteerd.");
		} else {
			if (!aUsers.getSelectionModel().getSelectedItem().getPositie().equals("ADMIN")) {
				alSelectie.setStyle("-fx-text-fill: red");
				alSelectie.setText("De user is geen admin.");
			} else {
				User u = aUsers.getSelectionModel().getSelectedItem();
				u.setPositie("HR");

				if (UserDAO.update(u)) {
					LogDAO.remAdmin(u.getLoginemail());
					initAppUsers();
					alSelectie.setStyle("-fx-text-fill: black");
					alSelectie.setText("Adminrechten succesvol afgeschaft!");
				} else {
					alSelectie.setStyle("-fx-text-fill: red");
					alSelectie.setText("Er is een technische fout opgelopen.");
				}

			}
		}
	}

	@FXML
	private void handleSetAdmin() {
		if (aUsers.getSelectionModel().getSelectedItems().size() == 0) {
			alSelectie.setStyle("-fx-text-fill: red");
			alSelectie.setText("Geen user geselecteerd.");
		} else {
			if (aUsers.getSelectionModel().getSelectedItem().getPositie().equals("ADMIN")) {
				alSelectie.setStyle("-fx-text-fill: red");
				alSelectie.setText("De user is reeds admin.");
			} else {
				User u = aUsers.getSelectionModel().getSelectedItem();
				u.setPositie("ADMIN");

				if (UserDAO.update(u)) {
					LogDAO.setAdmin(u.getLoginemail());
					initAppUsers();
					alSelectie.setStyle("-fx-text-fill: black");
					alSelectie.setText("User succesvol gepromoveerd!");
				} else {
					alSelectie.setStyle("-fx-text-fill: red");
					alSelectie.setText("Er is een technische fout opgelopen.");
				}

			}
		}
	}

	@FXML
	private void handleAppPassword() {
		if (aUsers.getSelectionModel().getSelectedItems().size() == 0) {
			alSelectie.setStyle("-fx-text-fill: red");
			alSelectie.setText("Geen user geselecteerd.");
		} else {

			User u = aUsers.getSelectionModel().getSelectedItem();
			String password = User.generatePassword();

			try {
				if (UserDAO.updatePassword(u.getLoginemail(), password)) {
					LogDAO.changedPassword(u.getLoginemail(), true);
					Email.sendPassword(u.getLoginemail(), password);
					alSelectie.setStyle("-fx-text-fill: black");
					alSelectie.setText("Nieuw wachtwoord succesvol gegenereerd!");
				} else {
					alSelectie.setStyle("-fx-text-fill: red");
					alSelectie.setText("Er is een technische fout opgelopen.");
				}
			} catch (UserOnbestaandException e) {
			}

		}
	}

	@FXML
	private void initAppUsers() {

		aUsers.setPlaceholder(new Label("Er zijn geen users."));

		acEmail.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<User, String> data) {
				return new SimpleStringProperty(data.getValue().getLoginemail());
			}
		});
		acNaam.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<User, String> data) {
				return new SimpleStringProperty(data.getValue().getVolleNaam());
			}
		});
		acAdmin.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<User, String> data) {
				return new SimpleStringProperty(data.getValue().getPositie().equals("ADMIN") ? "JA" : "NEE");
			}
		});

		ObservableList<User> list = FXCollections.observableArrayList(UserDAO.getAllExceptSession());
		aUsers.setItems(list);
	}

	@FXML
	private void clearAppLabel() {
		alSelectie.setText("");
	}

	@FXML
	private void clearWebLabel() {
		wlSelectie.setText("");
		glSelectie.setText("");
	}

	@FXML
	public void initialize() {
		initAppUsers();
		initWebsite();
	}

	// Website
	@FXML
	Tab website;
	@FXML
	TableView<Personeel> welAccount;
	@FXML
	TableColumn<Personeel, Integer> welId;
	@FXML
	TableColumn<Personeel, String> welNaam;
	@FXML
	TableColumn<Personeel, String> welEmail;
	@FXML
	TableView<Personeel> geenAccount;
	@FXML
	TableColumn<Personeel, Integer> geenId;
	@FXML
	TableColumn<Personeel, String> geenNaam;
	@FXML
	Button wbPassword;
	@FXML
	Button wbToevoegen;
	@FXML
	Label wlSelectie;
	@FXML
	Label glSelectie;

	@FXML
	private void handleWebPassword() {
		if (welAccount.getSelectionModel().getSelectedItems().size() == 0) {
			wlSelectie.setStyle("-fx-text-fill: red");
			wlSelectie.setText("Geen user geselecteerd.");
		} else {

			Personeel p = welAccount.getSelectionModel().getSelectedItem();
			String password = User.generatePassword();

			if (WebUserDAO.updatePassword(p.getAccount().getLoginemail(), password)) {
				LogDAO.changedPassword(p.getAccount().getLoginemail(), false);
				Email.sendPassword(p.getAccount().getLoginemail(), password);
				wlSelectie.setStyle("-fx-text-fill: black");
				wlSelectie.setText("Nieuw wachtwoord succesvol gegenereerd!");
				initWebsite();
			} else {
				wlSelectie.setStyle("-fx-text-fill: red");
				wlSelectie.setText("Er is een technische fout opgelopen.");
			}

		}
	}

	@FXML
	private void handleWebUser() {
		if (geenAccount.getSelectionModel().getSelectedItems().size() == 0) {
			glSelectie.setStyle("-fx-text-fill: red");
			glSelectie.setText("Geen user geselecteerd.");
		} else {

			Personeel p = geenAccount.getSelectionModel().getSelectedItem();
			String password = User.generatePassword();
			String loginemail = ("emp" + p.getPersId() + "@hotmail.com");

			if (PersoneelDAO.save(p) && WebUserDAO.save(loginemail, p, password)) {
				LogDAO.userToegevoegd(loginemail);
				Email.createdUser(loginemail, password);
				glSelectie.setStyle("-fx-text-fill: black");
				glSelectie.setText("Account succesvol aangemaakt!");
				initWebsite();
			} else {
				glSelectie.setStyle("-fx-text-fill: red");
				glSelectie.setText("Er is een technische fout opgelopen.");
			}

		}
	}

	@FXML
	private void initWebsite() {
		welAccount.setPlaceholder(new Label("Er zijn geen personeelsleden met account."));
		geenAccount.setPlaceholder(new Label("Er zijn geen personeelsleden."));

		welId.setCellValueFactory(new Callback<CellDataFeatures<Personeel, Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Personeel, Integer> data) {
				return new SimpleIntegerProperty(new Integer(data.getValue().getPersId())).asObject();
			}
		});
		welNaam.setCellValueFactory(new Callback<CellDataFeatures<Personeel, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Personeel, String> data) {
				return new SimpleStringProperty(data.getValue().getVolleNaam());
			}
		});
		welEmail.setCellValueFactory(new Callback<CellDataFeatures<Personeel, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Personeel, String> data) {
				return new SimpleStringProperty(data.getValue().getAccount().getLoginemail());
			}
		});
		welEmail.setCellFactory(column -> {
			return new TableCell<Personeel, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(item);
					if (item != null && !item.equals("")) {
						Tooltip t = new Tooltip(item);
						t.setMaxWidth(500);
						t.setWrapText(true);
						setTooltip(t);
					}
				}
			};
		});

		geenId.setCellValueFactory(new Callback<CellDataFeatures<Personeel, Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Personeel, Integer> data) {
				return new SimpleIntegerProperty(new Integer(data.getValue().getPersId())).asObject();
			}
		});
		geenNaam.setCellValueFactory(new Callback<CellDataFeatures<Personeel, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Personeel, String> data) {
				return new SimpleStringProperty(data.getValue().getVolleNaam());
			}
		});

		ObservableList<Personeel> list1 = FXCollections.observableArrayList(PersoneelDAO.getAllAccount());
		welAccount.setItems(list1);
		ObservableList<Personeel> list2 = FXCollections.observableArrayList(PersoneelDAO.getAllNoAccount());
		geenAccount.setItems(list2);
	}

}
