package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable {
    private DepartmentService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    private Department entity;

    @FXML
    private TextField inputFieldID;

    @FXML
    private TextField inputFieldName;

    @FXML
    private Label nameErrorLabel;

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

    private Department getFormData() {
        Department dep = new Department();
        ValidationException exception = new ValidationException("validation error");

        dep.setId(Utils.tryParseToInt(inputFieldID.getText()));

        if (inputFieldName.getText() == null || inputFieldName.getText().trim().equals("")){
            exception.addError("name", "field can't be empty");
        }
        dep.setName(inputFieldName.getText());

        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return dep;
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
        Constraints.setTextFieldMaxLength(inputFieldName, 30);
    }

    public void setDepartment(Department dep){
        this.entity = dep;
    }

    public void setDepartmentService(DepartmentService service){
        this.service = service;
    }

    public void updateFormData(){
        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        inputFieldID.setText(String.valueOf(entity.getId()));
        inputFieldName.setText(entity.getName());
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
