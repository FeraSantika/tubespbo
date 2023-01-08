package com.example.application.views.home;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.PlotOptionsPie;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("Home")
@Route(value = "Home", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSpacing(false);

//        add(new H2("GoSADARI ayo periksa sedini mungkin"));
        Image img = new Image("images/hasil kuisioner (6).png", "placeholder plant");
        img.setWidth("900px");
        add(img);
//        add(new Paragraph("Selamat datang, lakukan SADARI minimal sebulan sekali🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }


}
