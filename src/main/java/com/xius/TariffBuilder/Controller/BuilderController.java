package com.xius.TariffBuilder.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xius.TariffBuilder.Dto.UsrLoginRequest;
import com.xius.TariffBuilder.Dto.UsrLoginResponse;
import com.xius.TariffBuilder.Dto.UsrPrivilegeDTO;
import com.xius.TariffBuilder.Entity.ServicePlanPackMap;
import com.xius.TariffBuilder.UserService.SaveConfigService;
import com.xius.TariffBuilder.UserService.ServicePlanService;
import com.xius.TariffBuilder.UserService.TariffService;
import com.xius.TariffBuilder.UserService.UsrLoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BuilderController {

    @Autowired
    private ServicePlanService service;

    @Autowired
    private TariffService tariffService;

    @Autowired
    private SaveConfigService saveConfigService;

    @Autowired
    private UsrLoginService usrLoginService;

    // ================= LOGIN =================

    @GetMapping("/loginform")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new UsrLoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute UsrLoginRequest loginForm,
            Model model,
            HttpSession session) {

        // Validation
        if (loginForm.getNetworkLoginName() == null || loginForm.getNetworkLoginName().trim().isEmpty()) {
            model.addAttribute("message", "Please enter Network Name");
            model.addAttribute("loginForm", loginForm);
            return "login";
        }

        if (loginForm.getLoginId() == null || loginForm.getLoginId().trim().isEmpty()) {
            model.addAttribute("message", "Please enter Username");
            model.addAttribute("loginForm", loginForm);
            return "login";
        }

        if (loginForm.getPassword() == null || loginForm.getPassword().trim().isEmpty()) {
            model.addAttribute("message", "Please enter Password");
            model.addAttribute("loginForm", loginForm);
            return "login";
        }

        UsrLoginResponse response = usrLoginService.authenticate(loginForm);

        // Invalid login
        if (response == null || response.getLoginId() == null) {
            model.addAttribute("message", "Invalid Credentials");
            model.addAttribute("loginForm", loginForm);
            return "login";
        }

        // Extract privileges safely
        List<String> privilegeIds = response.getPrivileges() != null
                ? response.getPrivileges()
                        .stream()
                        .map(UsrPrivilegeDTO::getPrivilegeId)
                        .collect(Collectors.toList())
                : List.of();

        // Store in session
        session.setAttribute("username", response.getLoginId());
        session.setAttribute("networkId", response.getNetworkId());
        session.setAttribute("privileges", response.getPrivileges());
        session.setAttribute("privilegeIds", privilegeIds);

        return "redirect:/builder";
    }

    @GetMapping("/builder")
    public String builderHome(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);

        return "builder/step1"; // just load layout
    }

    // ================= ADMIN =================

    @GetMapping("/builder/admin")
    public String adminPage(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);

        List<String> tariffList = tariffService.getTariffPackages();

        model.addAttribute("tariff", tariffList);

        return "builder/admin";
    }

    // @PostMapping("/admin/updateStatus")
    // @ResponseBody
    // public ResponseEntity<String> updateStatus(@RequestBody TariffEntity req) {

    // tariffService.updateStatus(req.getTariffPackageId(), req.getStatus());

    // return ResponseEntity.ok("success");
    // }

    // ================= STEPS =================

    @GetMapping("/builder/step1")
    public String step1(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);
        return "builder/step1";
    }

    @GetMapping("/builder/step2")
    public String step2(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);
        return "builder/step2";
    }

    @GetMapping("/builder/step2/filter")
    @ResponseBody
    public List<ServicePlanPackMap> getTpPlans(@RequestParam String types,
            HttpSession session) {

        Long networkId = (Long) session.getAttribute("networkId");
        return service.getPlans(networkId, types);
    }

    @GetMapping("/builder/step3")
    public String step3(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);
        return "builder/step3";
    }

    @GetMapping("/builder/step3/filter")
    @ResponseBody
    public List<ServicePlanPackMap> getDAtpPlans(@RequestParam String types,
            HttpSession session) {

        Long networkId = (Long) session.getAttribute("networkId");
        return service.getDAtpPlans(networkId, types);
    }

    @GetMapping("/builder/step4")
    public String step4(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);
        return "builder/step4";
    }

    @GetMapping("/builder/step4/filter")
    @ResponseBody
    public List<ServicePlanPackMap> getAAtpPlans(@RequestParam String types,
            HttpSession session) {

        Long networkId = (Long) session.getAttribute("networkId");
        return service.getAAtpPlans(networkId, types);
    }

    @GetMapping("/builder/step5")
    public String step5(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);
        return "builder/step5";
    }

    // ================= HIERARCHY =================

    @GetMapping("/admin/hierarchy/{tpName}")
    @ResponseBody
    public Object getHierarchy(
            @PathVariable String tpName) {

        return tariffService.getHierarchy(tpName);
    }

    // ================= SAVE CONFIG =================

    @PostMapping("/prepareSaveConfig")
    public ResponseEntity<?> prepareSaveConfig(
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        Long networkId = (Long) session.getAttribute("networkId");

        Map<String, Object> response = saveConfigService.prepareConfig(request, username, networkId);

        return ResponseEntity.ok(response);
    }

    // ================= COMMON =================

    private void setCommonData(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("networkId", session.getAttribute("networkId"));
        model.addAttribute("privileges", session.getAttribute("privileges"));
        model.addAttribute("privilegeIds", session.getAttribute("privilegeIds"));
    }

    private boolean isNotLoggedIn(HttpSession session) {
        return session.getAttribute("username") == null;
    }

    // @GetMapping("/tariffPackages")
    // @ResponseBody
    // public List<TariffEntity> getTariffPackages() {
    // return tariffService.getTariffPackages();
    // }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginform";
    }
}