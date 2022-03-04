package org.foi.nwtis.lmrkonjic.app3.controller;

import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("index")
@Controller
public class IndexKontroler
{

    @GET
    @View("index.jsp")
    public String index()
    {
        return "../../index.jsp";
    }

    

}
