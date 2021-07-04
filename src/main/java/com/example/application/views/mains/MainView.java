package com.example.application.views.mains;

import com.example.application.models.Reservation;
import com.example.application.models.SystemUser;
import com.example.application.services.ReservationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private final ReservationService reservationService;
    Grid<Reservation> grid = new Grid<>(Reservation.class); //the part where the main grid is defined
    TextField txtFilter = new TextField();
    Dialog dialogReservation=new Dialog();
    Binder<Reservation> binder = new Binder<>();
    Long itemIdForEdition=0L;
    Long loggedInSystemUserId;

    public MainView(ReservationService reservationService){
        this.reservationService = reservationService;
        if (VaadinSession.getCurrent().getSession().getAttribute("LoggedInSystemUserId")==null){
            UI.getCurrent().getPage().setLocation("/login");
        }
        else {
            System.out.println("Signed in with ID");
            System.out.println(VaadinSession.getCurrent().getSession().getAttribute("LoggedInSystemUserId").toString());
            loggedInSystemUserId=Long.valueOf(VaadinSession.getCurrent().getSession().getAttribute("LoggedInSystemUserId").toString());
        }

        Button buttonMakeReservation = new Button("Make a Reservation"); //make reservation button
        Button buttonSignOut = new Button("Sign Out");  //sign out button
        buttonSignOut.addClickListener(buttonClickEvent -> {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().setLocation("/login");
        });
        buttonSignOut.getStyle().set("marginLeft", "1200px");

        txtFilter.setPlaceholder("Enter Carpet Field Name"); //field based filtering
        Button buttonSorting = new Button("Carpet Field Search", VaadinIcon.SEARCH.create());
        buttonSorting.addClickListener(buttonClickEvent -> {
            refreshData(txtFilter.getValue());
        });

        HorizontalLayout filterGroup = new HorizontalLayout();
        filterGroup.add(txtFilter,buttonSorting,buttonMakeReservation,buttonSignOut);
        dialogReservation.setModal(true);

        TextField txtName = new TextField("Name"); //items in the dialog
        TextField txtSurname = new TextField("Surname");
        TextField txtEmail = new TextField("Email");

        Button buttonClear = new Button("Clear", event -> { //clear button
            txtName.clear();
            txtSurname.clear();
            txtEmail.clear();
        });

        ComboBox<String> comboboxCarpetField = new ComboBox<>(); //field list
        comboboxCarpetField.setItems("Camp Nou Carpet Field - 25*45 square meters - Hourly Price = 300$",
                               "Allianz Carpet Field - 25*40 square meters - Hourly Price = 250$",
                               "Juventus Carpet Field - 20*40 square meters - Hourly Price = 275$",
                               "Santiago Bernabéu Carpet Field - 20*40 square meters - Hourly Price = 185$",
                               "Celtic Park Carpet Field - 30*50 square meters - Hourly Price = 300$",
                               "Old Trafford Carpet Field - 30*50 square meters - Hourly Price = 350$",
                               "Princes Park Carpet Field - 20*40 square meters - Hourly Price = 175$");
        comboboxCarpetField.setLabel("Carpet Field");

        DatePicker datePicker = new DatePicker(); //day picker from calendar
        datePicker.setLabel("Date");
        LocalDate now = LocalDate.now();
        datePicker.setValue(now);
        datePicker.setMin(LocalDate.now());


        TimePicker timeStart = new TimePicker(); //timeStart time selector
        timeStart.setLabel("Start Time");
        timeStart.setMinTime(LocalTime.now());

        TimePicker timeFinish = new TimePicker(); //end time selector
        timeFinish.setLabel("End Time");
        timeFinish.setMinTime(LocalTime.now().plusHours(1));


        binder.bind(txtName, Reservation::getName, Reservation::setName); // adding the entered data to the grid
        binder.bind(txtSurname, Reservation::getSurname, Reservation::setSurname);
        binder.bind(txtEmail, Reservation::getEmail, Reservation::setEmail);
        binder.bind(datePicker, Reservation::getDate, Reservation::setDate);
        binder.bind(timeStart, Reservation::getStart, Reservation::setStart);
        binder.bind(timeFinish, Reservation::getFinish, Reservation::setFinish);
        binder.bind(comboboxCarpetField, Reservation::getCarpetField, Reservation::setCarpetField);

        FormLayout formLayout = new FormLayout(); //popup dialog
        formLayout.add(txtName,txtSurname,txtEmail,datePicker,timeStart,timeFinish,comboboxCarpetField);

        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        Button buttonCancel = new Button("Cancel"); // cancel button
        Button buttonSave = new Button("Save"); //save button
        buttonCancel.addClickListener(buttonClickEvent -> {
            dialogReservation.close();
        });
        buttonSave.addClickListener(buttonClickEvent -> {
            Notification.show("Reservation Added Successfully");
            Reservation reservation = new Reservation();
            try {
                binder.writeBean(reservation);
            } catch (ValidationException e) {
                e.printStackTrace();
            }

            reservation.setId(itemIdForEdition);

            SystemUser loggedInSystemUser=new SystemUser(); //saving part of the data to the logged in user
            loggedInSystemUser.setId(loggedInSystemUserId);
            reservation.setSystemUser(loggedInSystemUser);
            reservationService.save(reservation);
            refreshData(txtFilter.getValue().toString());
            dialogReservation.close();
        });


        horizontalLayout.add(buttonCancel,buttonClear,buttonSave);
        dialogReservation.add(new H3("New Reservation"),formLayout,horizontalLayout); //dialog window layout

        buttonMakeReservation.addClickListener(buttonClickEvent -> {    //click event of reservation button
            itemIdForEdition=0L;
            binder.readBean(new Reservation());
            dialogReservation.open();
        });
        refreshData(txtFilter.getValue().toString());

        grid.removeColumnByKey("id");
        grid.setSelectionMode(Grid.SelectionMode.NONE); //turn off selectable grid
        grid.setColumns("name", "surname", "email", "date", "start", "finish", "carpetField");
        grid.addComponentColumn(item -> createRemoveButton(grid, item))
                .setHeader("Actions");


        Image image = new Image("https://i.hizliresim.com/skqm1lw.jpg","carpetField");
        Image image1 = new Image("https://i.hizliresim.com/fmt613c.jpg","carpetField1");
        Image image2 = new Image("https://i.hizliresim.com/pxnh34l.jpg","carpetField2");
        image.getStyle().set("marginLeft", "120px");


        HorizontalLayout images=new HorizontalLayout(); //pictures at the bottom of the page
        images.add(image,image1,image2);
        add(new H2("Carpet Field Operation"),filterGroup,grid,images);
    }

    private void refreshData(String filter){
        List<Reservation> reservationList = new ArrayList<>(); //Adding the data entered by the user to the list
        reservationList.addAll(reservationService.getList(filter,loggedInSystemUserId));
        grid.setItems(reservationList);
    }

    private HorizontalLayout createRemoveButton(Grid<Reservation> grid, Reservation item) {
        @SuppressWarnings("hunchecked")
        Button buttonDeleteReservation = new Button("Delete"); //delete reservation button
        buttonDeleteReservation.addClickListener(buttonClickEvent -> {

            ConfirmDialog dialog = new ConfirmDialog("Reservation Deletion",
                    "Are you sure you want to delete the reservation?", "Delete", confirmEvent -> {
                Notification.show("Reservation Delete Successfully");
                reservationService.delete(item);
                refreshData(txtFilter.getValue().toString());
            },
                    "Cancel", cancelEvent -> {
            });
            dialog.setConfirmButtonTheme("error primary");
            dialog.open();
        });

        Button buttonEditReservation = new Button("Edit"); // edit reservation button
        buttonEditReservation.addClickListener(buttonClickEvent -> {
            itemIdForEdition=item.getId();
            binder.readBean(item);
            dialogReservation.open();
        });

        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.add(buttonEditReservation,buttonDeleteReservation);
        return horizontalLayout;
    }
}

