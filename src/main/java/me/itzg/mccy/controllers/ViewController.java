package me.itzg.mccy.controllers;

import me.itzg.mccy.config.MccyBuildSettings;
import me.itzg.mccy.config.MccySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 12/28/2015
 */
@Controller
public class ViewController {

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private MccyBuildSettings mccyBuildSettings;

    @ModelAttribute("deploymentPoweredBy")
    public MccySettings.DeploymentPoweredBy deploymentPoweredBy() {
        return mccySettings.getDeploymentPoweredBy();
    }

    @ModelAttribute("build")
    public MccyBuildSettings getBuildSettings() {
        return mccyBuildSettings;
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping("/login")
    ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam Map<String,String> reqParams) {
        response.addHeader("x-login", "true");

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                .getName());

        final ModelAndView modelAndView = new ModelAndView()
                .addObject("csrf", csrf);
        modelAndView.setViewName("login");

        modelAndView.addObject("error", reqParams.containsKey("error"));

        return modelAndView;
    }
}
