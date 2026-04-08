// ═══════════════════════════════════════════════════════
//  PRIVILEGE-BASED MODULE VISIBILITY
//
//  Privilege IDs (to be set when auth API is wired up):
//    1  →  BUILDER only
//    2  →  APPROVER only
//    3  →  BUILDER + APPROVER
//
//  Currently defaults to showing both nodes for dev/demo.
//  Replace the constant below (or fetch it from session/API)
//  once the privilege endpoint is ready.
// ═══════════════════════════════════════════════════════

const PRIVILEGE_ID = parseInt(sessionStorage.getItem('privilegeId') || '3', 10);

// Maps privilege id → which main-rail nodes are visible
const PRIVILEGE_MAP = {
    1: ['builder'],
    2: ['approver'],
    3: ['builder', 'approver'],
};

// ── Apply privilege on load ──
window.addEventListener('DOMContentLoaded', () => {

    applyPrivilege();
    restoreActiveModule();
    restoreConfigName();
});

function applyPrivilege() {

    const allowed = PRIVILEGE_MAP[PRIVILEGE_ID] || ['builder'];

    const builderNode = document.getElementById('mn-builder');
    const approverNode = document.getElementById('mn-approver');

    if (!allowed.includes('builder') && builderNode) builderNode.classList.add('hidden-node');
    if (!allowed.includes('approver') && approverNode) approverNode.classList.add('hidden-node');

    // If only one module is allowed, auto-select it and hide main rail
    // (single-privilege users see no wasted chrome)
    if (allowed.length === 1) {
        document.getElementById('mainRail').style.width = '0';
        document.getElementById('mainRail').style.overflow = 'hidden';
        document.getElementById('mainRail').style.padding = '0';
    }
}

// ── Restore active module based on current URL ──
function restoreActiveModule() {

    const path = window.location.pathname;

    if (path.startsWith('/builder/admin')) {
        setModuleUI('approver');
    } else {
        setModuleUI('builder');
    }
}

// ── Activate module (called from main-rail anchor click) ──
function activateModule(module, el) {

    const allowed = PRIVILEGE_MAP[PRIVILEGE_ID] || ['builder'];
    if (!allowed.includes(module)) {
        return false; // block navigation if not privileged
    }

    setModuleUI(module);

    // Allow the anchor's href to fire
    return true;
}

function setModuleUI(module) {

    const stepRail = document.getElementById('stepRail');
    const sidebar = document.getElementById('sidebar');
    const builderNode = document.getElementById('mn-builder');
    const approverNode = document.getElementById('mn-approver');

    // ── Active node highlight ──
    if (builderNode) builderNode.classList.toggle('active', module === 'builder');
    if (approverNode) approverNode.classList.toggle('active', module === 'approver');

    if (module === 'builder') {
        // Show step rail + sidebar
        if (stepRail) stepRail.classList.remove('collapsed');
        if (sidebar) sidebar.classList.remove('collapsed');
        if (footerActions) footerActions.style.display = 'flex';
    } else {
        // Approver — collapse step rail + sidebar (they're irrelevant)
        if (stepRail) stepRail.classList.add('collapsed');
        if (sidebar) sidebar.classList.add('collapsed');
        if (footerActions) footerActions.style.display = 'none';
    }
}

// ═══════════════════════════════════════════════════════
//  PRIVILEGE SETTER  (call this from your auth callback)
//  e.g.  setPrivilege(1)  after login API returns priv id
// ═══════════════════════════════════════════════════════
function setPrivilege(id) {
    sessionStorage.setItem('privilegeId', String(id));
    // reload so applyPrivilege() runs fresh
    window.location.reload();
}

// ═══════════════════════════════════════════════════════
//  STATE HELPERS
// ═══════════════════════════════════════════════════════
function getState() {

    const defaultState = {
        s2: [],
        s3: [],
        s4: [],
        price: "",
        publicityCode: "",
        endDate: "",
        isCorporate: false
    };

    const stored = sessionStorage.getItem('state');
    return stored ? JSON.parse(stored) : defaultState;
}

function saveState(state) {
    sessionStorage.setItem('state', JSON.stringify(state));
}

// ── Restore config name across steps ──
function restoreConfigName() {

    const input = document.getElementById('configName');
    if (!input) return;

    const savedName = sessionStorage.getItem('configName') || '';
    input.value = savedName;

    input.addEventListener('input', () => {
        sessionStorage.setItem('configName', input.value);
    });
}

// ═══════════════════════════════════════════════════════
//  STEP ACCESS GUARD
// ═══════════════════════════════════════════════════════
function checkStepAccess(targetStep) {

    const currentPath = window.location.pathname;

    if (currentPath.includes(`step${targetStep}`)) return true;
    if (targetStep === 1) return true;

    const pkgType = sessionStorage.getItem('pkgType');
    const state = JSON.parse(sessionStorage.getItem('state') || '{}');

    if (!pkgType) {
        alert("Please select PREPAID or POSTPAID in Step 1");
        return false;
    }

    const hasStep2Data = state.s2 && Array.isArray(state.s2) && state.s2.length > 0;

    if (targetStep > 2 && !hasStep2Data) {
        alert("Please select Service Plan in Step 2");
        return false;
    }

    return true;
}

// ═══════════════════════════════════════════════════════
//  USER MENU
// ═══════════════════════════════════════════════════════
function toggleUserMenu() {
    const dropdown = document.getElementById("userDropdown");
    dropdown.classList.toggle("active");
}

document.addEventListener("click", function (e) {
    const menu = document.querySelector(".user-menu");
    if (menu && !menu.contains(e.target)) {
        const dd = document.getElementById("userDropdown");
        if (dd) dd.classList.remove("active");
    }
});

// ═══════════════════════════════════════════════════════
//  SAVE PACKAGE CONFIGURATION
// ═══════════════════════════════════════════════════════
async function saveConfiguration() {

    const configName = document.getElementById("configName").value;

    if (!configName) {
        alert("Enter Configuration Name");
        return;
    }

    const state = JSON.parse(sessionStorage.getItem("state"));

    if (!state?.s2?.length) {
        alert("Step 2 required");
        return;
    }


    const chargeId = configName + "_PR";


    function formatDateToMMDDYYYY(dateStr) {

        if (!dateStr) return "12/31/2030";

        const [year, month, day] =
            dateStr.split("-");

        return `${month}/${day}/${year}`;
    }


    const payload = {

        username: "USER1",   // keep only username in request

        packageType:
            sessionStorage.getItem("pkgType"),

        tariffPackCategory:
            sessionStorage.getItem("pkgSubType")
            || "GENERAL",

        tariffPackageDesc:
            configName,

        endDate:
            formatDateToMMDDYYYY(state.endDate),

        publicityId:
            state.publicityCode
            || "DEFAULT_PUB",

        chargeId:
            chargeId,

        isCorporateYn:
            state.isCorporate ? "Y" : "N",

        tariffPlanId:
            Number(state.s2[0].id),

        defaultAtps:
            state.s3.map(item => ({

                servicePackageId:
                    Number(item.id),

                chargeId:
                    chargeId
            })),

        allowedAtps:
            state.s4.map(item => ({

                servicePackageId:
                    Number(item.id),

                chargeId:
                    chargeId
            }))
    };


    console.log("REQUEST", payload);


    try {

        const response =
            await fetch("/prepareSaveConfig", {   // changed endpoint

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });


        const result = await response.json();


        console.log("RESPONSE", result);


        if (!response.ok || result.error) {

            alert(result.error || "Validation failed");

            return;
        }


        alert("Configuration Prepared (JSON stored)");

        clearBuilderSession();

        window.location.href =
            "/builder/step1";

    }

    catch (error) {

        console.error(error);

        alert("Server error");
    }
}

function clearBuilderSession() {
    sessionStorage.removeItem('state');
    sessionStorage.removeItem('selectedSvcs_s2');
    sessionStorage.removeItem('selectedSvcs_s3');
    sessionStorage.removeItem('selectedSvcs_s4');
    sessionStorage.removeItem('configName');
    sessionStorage.removeItem('pkgType');
    sessionStorage.removeItem('pkgSubType');
}

// ═══════════════════════════════════════════════════════
//  HIERARCHY MODAL
// ═══════════════════════════════════════════════════════
function viewTree() {
    const state = getState();
    const name = document.getElementById('configName').value || 'Unnamed Package';
    const type = sessionStorage.getItem('pkgType') || '';
    const sub = sessionStorage.getItem('pkgSubType') || 'NORMAL';

    document.getElementById('treeName').textContent = name;
    document.getElementById('treeMeta').textContent = `${type ? type + ' | ' : ''}${sub} | ${state.isCorporate ? 'Corporate' : 'Retail'}`;
    document.getElementById('treeMain').textContent = `📦 Main Service Plan: ${state.s2 && state.s2[0] ? state.s2[0].name : 'None'}`;
    document.getElementById('treeDatp').textContent = `➕ DATP Components: ${(state.s3 || []).length} items`;
    document.getElementById('treeAatp').textContent = `🛒 AATP Components: ${(state.s4 || []).length} items`;
    document.getElementById('treeCharge').innerHTML = `<b>Charge: RM ${state.price || '0.00'}</b> | <b>Ends: ${state.endDate || 'Permanent'}</b>`;

    document.getElementById('treeModal').classList.add('open');
}

function closeTree() {
    document.getElementById('treeModal').classList.remove('open');
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeTree();
});

// ═══════════════════════════════════════════════════════
//  LOGOUT
// ═══════════════════════════════════════════════════════
function logout() {
    sessionStorage.clear();
    window.location.href = '/logout';
}

// ═══════════════════════════════════════════════════════
//  ADMIN ACTIONS (Approve/Reject) in Approver Module
// ═══════════════════════════════════════════════════════
function handleAction(tariffPackageId, status) {
    const message = status === 'A' ? 'Approved' : 'Rejected';

    fetch('/admin/updateStatus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            tariffPackageId: tariffPackageId,
            status: status
        })
    })
        .then(res => {
            if (res.ok) {
                alert(message);
                document.getElementById('card-' + tariffPackageId).remove();
            } else {
                alert('Error!');
            }
        });
}