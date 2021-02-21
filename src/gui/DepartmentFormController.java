package gui;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

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
    public void onBtnSaveAction(){
        System.out.println("onBtnSaveAction");
    }

    @FXML
    public void onBtnCancelAction(){
        System.out.println("onBtnCancelAction");

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

    public void updateFormData(){
        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        inputFieldID.setText(String.valueOf(entity.getId()));
        inputFieldName.setText(entity.getName());
    }
}
