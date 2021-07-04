package com.example.application.views.mains;

import com.example.application.models.SystemUser;
import com.example.application.services.SystemUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


@Route
public class LoginView extends VerticalLayout {
    private final SystemUserService systemUserService;

    public LoginView(SystemUserService systemUserService){
        this.systemUserService = systemUserService;
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(loginEvent -> {

            SystemUser result1 = systemUserService.login(loginEvent.getUsername(),loginEvent.getPassword());
            if (result1.getId()!=null) {
                VaadinSession.getCurrent().getSession().setAttribute("LoggedInSystemUserId",result1.getId());
                UI.getCurrent().getPage().setLocation("/");
            }
            else {
                loginForm.setError(true);
            }
        });
        add(loginForm);
    }
}
