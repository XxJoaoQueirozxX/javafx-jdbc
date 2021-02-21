package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {
    private SellerService service;

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
    }

    public void setSeller(Seller seller){
        this.entity = seller;
    }

    public void setSellerService(SellerService service){
        this.service = service;
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
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    public void setErrorsMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();
        if (fields.contains("name")){
            nameErrorLabel.setText(errors.get("name"));
        }
    }
}
