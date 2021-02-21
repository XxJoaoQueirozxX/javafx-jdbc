package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class SellerFormController implements Initializable {
    private SellerService service;
    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    private Seller entity;

    @FXML
    private TextField inputFieldID;

    @FXML
    private TextField inputFieldName;

    @FXML
    private TextField inputFieldEmail;

    @FXML
    private TextField inputFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label birthDateErrorLabel;

    @FXML
    private Label salaryErrorLabel;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;


    private ObservableList<Department> obsList;

    @FXML
    public void onBtnSaveAction(ActionEvent event){
        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        if (service == null){
            throw new IllegalStateException("Service was null");
        }
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListener();
            Utils.currentStage(event).close();
        }catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }catch (ValidationException excep){
            setErrorsMessages(excep.getErrors());
        }
    }

    private void notifyDataChangeListener() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Seller getFormData() {
        Seller seller = new Seller();
        ValidationException exception = new ValidationException("validation error");

        seller.setId(Utils.tryParseToInt(inputFieldID.getText()));

        if (inputFieldName.getText() == null || inputFieldName.getText().trim().equals("")){
            exception.addError("name", "field can't be empty");
        }
        seller.setName(inputFieldName.getText());

        if (inputFieldEmail.getText() == null || inputFieldEmail.getText().trim().equals("")){
            exception.addError("email", "field can't be empty");
        }
        seller.setEmail(inputFieldEmail.getText());


        if (dpBirthDate.getValue() == null){
            exception.addError("birthDate", "field can't be empty");
        }else{
            seller.setBirthDate(dpBirthDate.getValue());
        }

        if (inputFieldBaseSalary.getText() == null || inputFieldBaseSalary.getText().trim().equals("")){
            exception.addError("baseSalary", "field can't be empty");
        }

        seller.setBaseSalary(Utils.tryParseToDouble(inputFieldBaseSalary.getText()));

        seller.setDepartment(comboBoxDepartment.getValue());


        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return seller;
    }

    @FXML
    public void onBtnCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(inputFieldID);
        Constraints.setTextFieldMaxLength(inputFieldName, 70);
        Constraints.setTextFieldDouble(inputFieldBaseSalary);
        Constraints.setTextFieldMaxLength(inputFieldEmail, 60);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    public void setSeller(Seller seller){
        this.entity = seller;
    }

    public void setServices(SellerService service, DepartmentService departmentService){
        this.service = service;
        this.departmentService = departmentService;
    }

    public void updateFormData(){
        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        inputFieldID.setText(String.valueOf(entity.getId()));
        inputFieldName.setText(entity.getName());
        inputFieldEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        inputFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        dpBirthDate.setValue(entity.getBirthDate());
        if (entity.getDepartment() == null){
            comboBoxDepartment.getSelectionModel().selectFirst();
        }else{
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void loadAssociatedObjects(){
        if (departmentService == null){
            throw new IllegalStateException("Department service was null");
        }
        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);
    }


    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    public void setErrorsMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        nameErrorLabel.setText((fields.contains("name"))? errors.get("name"):"");
        emailErrorLabel.setText((fields.contains("email"))? errors.get("email"):"");
        salaryErrorLabel.setText((fields.contains("baseSalary"))? errors.get("baseSalary"):"");
        birthDateErrorLabel.setText((fields.contains("birthDate"))? errors.get("birthDate"):"");
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }


}
