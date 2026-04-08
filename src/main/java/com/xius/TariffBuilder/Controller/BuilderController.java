package com.xius.TariffBuilder.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.xius.TariffBuilder.Dto.UsrLoginRequest;
import com.xius.TariffBuilder.Dto.UsrLoginResponse;
import com.xius.TariffBuilder.Dto.UsrPrivilegeDTO;
import com.xius.TariffBuilder.Entity.ServicePlanPackMap;
import com.xius.TariffBuilder.Entity.TariffEntity;
import com.xius.TariffBuilder.Entity.TariffHierarchy;
import com.xius.TariffBuilder.UserService.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class BuilderController {

    @Autowired
    private ServicePlanService service;

    @Autowired
    private TariffService tariffService;

    @Autowired
    private TariffSaveService tariffSaveService;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private SaveConfigService saveConfigService;

    @Autowired
    private UsrLoginService usrLoginService;

    @Autowired
    private ServicePlan service1;

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
        session.setAttribute("privilegeIds", privilegeIds);

        return "redirect:/builder/step1";
    }

    // ================= ADMIN =================

    @GetMapping("/builder/admin")
    public String adminPage(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);

        List<TariffEntity> tariffList = tariffService.getTariffPackages();
        model.addAttribute("tariff", tariffList);

        return "builder/admin";
    }

    @GetMapping("/builder/pendingtariff")
    public String pendingTariff(HttpSession session, Model model) {

        if (isNotLoggedIn(session))
            return "redirect:/loginform";

        setCommonData(session, model);

        model.addAttribute("tariff", tariffService.getPendingTariffs());

        return "builder/admin";
    }

    @PostMapping("/admin/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@RequestBody TariffEntity req) {

        tariffService.updateStatus(req.getTariffPackageId(), req.getStatus());

        return ResponseEntity.ok("success");
    }

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

    @GetMapping("/admin/hierarchy/{tariffPackageId}")
    @ResponseBody
    public List<TariffHierarchy> getHierarchy(@PathVariable Long tariffPackageId,
                                              HttpSession session) {

        Long networkId = (Long) session.getAttribute("networkId");

        return hierarchyService.getHierarchy(networkId, tariffPackageId);
    }

    // ================= SERVICE PLAN =================

    @PostMapping("/servicePlanDetails")
    @ResponseBody
    public Map<String, Object> getServicePlanDetails(
            @RequestBody Map<String, Object> request) {

        Long servicePackageId = Long.valueOf(request.get("servicePackageId").toString());
        Long networkId = Long.valueOf(request.get("networkId").toString());

        return service1.fetchPlanDetails(servicePackageId, networkId);
    }

    // ================= SAVE CONFIG =================

    @PostMapping("/prepareSaveConfig")
    public ResponseEntity<?> prepareSaveConfig(
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        Long networkId = (Long) session.getAttribute("networkId");

        Map<String, Object> response =
                saveConfigService.prepareConfig(request, username, networkId);

        return ResponseEntity.ok(response);
    }

    // ================= COMMON =================

    private void setCommonData(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("networkId", session.getAttribute("networkId"));
    }

    private boolean isNotLoggedIn(HttpSession session) {
        return session.getAttribute("username") == null;
    }

    @GetMapping("/tariffPackages")
    @ResponseBody
    public List<TariffEntity> getTariffPackages() {
        return tariffService.getTariffPackages();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginform";
    }
}