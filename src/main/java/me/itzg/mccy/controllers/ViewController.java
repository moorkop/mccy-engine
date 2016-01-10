package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Geoff Bourne
 * @since 12/28/2015
 */
@Controller
public class ViewController {

    @Autowired
    private MccySettings mccySettings;

    @ModelAttribute("deploymentPoweredBy")
    public MccySettings.DeploymentPoweredBy deploymentPoweredBy() {
        return mccySettings.getDeploymentPoweredBy();
    }

    @ModelAttribute("csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class
                .getName());
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping("/login")
    ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.addHeader("x-login", "true");

        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");

        return modelAndView;
    }
}
