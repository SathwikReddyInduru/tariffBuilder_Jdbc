function applyPrivilege() {

    const builderNode = document.getElementById('mn-builder');
    const approverNode = document.getElementById('mn-approver');

    const hasBuilder = PRIVILEGE_IDS.includes("P26125");
    const hasApprover = PRIVILEGE_IDS.includes("P26126");

    // Hide nodes individually
    if (!hasBuilder && builderNode) {
        builderNode.style.display = "none";
    }

    if (!hasApprover && approverNode) {
        approverNode.style.display = "none";
    }
}

// ── Apply privilege on load ──
window.addEventListener('DOMContentLoaded', () => {

    applyPrivilege();
    restoreActiveModule();
    restoreConfigName();
});

// ── Restore active module based on current URL ──
function restoreActiveModule() {

    const hasBuilder = PRIVILEGE_IDS.includes("P26125");
    const hasApprover = PRIVILEGE_IDS.includes("P26126");

    const path = window.location.pathname;

    // If already in admin
    if (path.startsWith('/builder/admin')) {
        setModuleUI('approver');
        return;
    }

    // If already in builder
    if (path.startsWith('/builder/step')) {
        setModuleUI('builder');
        return;
    }

    // FIRST LOAD DECISION
    if (!hasBuilder && hasApprover) {
        window.location.href = "/builder/admin";
    } else if (hasBuilder) {
        window.location.href = "/builder/step1";
    }
}

// ── Activate module (called from main-rail anchor click) ──
function activateModule(module, el) {

    const hasBuilder = PRIVILEGE_IDS.includes("P26125");
    const hasApprover = PRIVILEGE_IDS.includes("P26126");

    if (module === 'builder' && !hasBuilder) return false;
    if (module === 'approver' && !hasApprover) return false;

    setModuleUI(module);
    return true;
}

function setModuleUI(module) {

    const stepRail = document.getElementById('stepRail');
    const sidebar = document.getElementById('sidebar');
    const builderNode = document.getElementById('mn-builder');
    const approverNode = document.getElementById('mn-approver');
    const configInput = document.getElementById('configName');

    // ── Active node highlight ──
    if (builderNode) builderNode.classList.toggle('active', module === 'builder');
    if (approverNode) approverNode.classList.toggle('active', module === 'approver');

    if (module === 'builder') {
        // Show step rail + sidebar
        if (stepRail) stepRail.classList.remove('collapsed');
        if (sidebar) sidebar.classList.remove('collapsed');
        if (footerActions) footerActions.style.display = 'flex';
        if (configInput) configInput.style.display = 'block';
    } else {
        // Approver — collapse step rail + sidebar (they're irrelevant)
        if (stepRail) stepRail.classList.add('collapsed');
        if (sidebar) sidebar.classList.add('collapsed');
        if (footerActions) footerActions.style.display = 'none';
        if (configInput) configInput.style.display = 'none';
    }
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

document.addEventListener("click", function(e) {
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

    if (!state ?.s2 ?.length) {
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

        username: USERNAME,

        submittedOn: new Date().toLocaleDateString('en-GB', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        }),

        packageType: sessionStorage.getItem("pkgType"),

        tariffPackCategory: sessionStorage.getItem("pkgSubType") ||
            "GENERAL",

        tariffPackageDesc: configName,

        charge: state.price || "0.00",

        endDate: formatDateToMMDDYYYY(state.endDate),

        publicityId: state.publicityCode ||
            "DEFAULT_PUB",

        chargeId: chargeId,

        isCorporateYn: state.isCorporate,

        tariffPlanId: Number(state.s2[0].id),

        tariffPlanName: state.s2[0].name,

        defaultAtps: state.s3.map(item => ({

            servicePackageId: Number(item.id),

            chargeId: chargeId,

            packageName: item.name,

            validity: item.validity,

            midnightExpiry: item.midnightExpiry,

            renewal: item.renewal,

            rental: item.rental || 0,

            maxCount: item.maxCount || 0,

            freeCycles: item.freeCycles || 0
        })),

        additionalAtps: state.s4.map(item => ({

            servicePackageId: Number(item.id),

            chargeId: chargeId,

            packageName: item.name,

            validity: item.validity,

            midnightExpiry: item.midnightExpiry,

            renewal: item.renewal,

            rental: item.rental || 0,

            maxCount: item.maxCount || 0,

            freeCycles: item.freeCycles || 0
        }))
    };

    console.log("REQUEST", payload);

    try {

        const response =
            await fetch("/prepareSaveConfig", {

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
    } catch (error) {
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
//  ADMIN — TWO-PANE APPROVER UI (with working arrows)
// ═══════════════════════════════════════════════════════

function selectPackage(cardElement) {
    const tpName = cardElement.dataset.tpname;
    const isAlreadySelected = cardElement.classList.contains('selected');

    document.querySelectorAll('.approval-card').forEach(c => c.classList.remove('selected'));

    if (isAlreadySelected) {
        document.getElementById('hierarchy-view').classList.add('hidden');
        document.getElementById('no-selection').classList.remove('hidden');
        return;
    }

    cardElement.classList.add('selected');
    loadHierarchy(tpName);
}

function loadHierarchy(tpName) {

    fetch('/admin/hierarchy/' + tpName)
        .then(res => res.json())
        .then(tp => {
            if (!tp || !tp.data) {
                alert("No hierarchy data found");
                return;
            }

            const data = tp.data || {};

            // Switch views
            document.getElementById('no-selection').classList.add('hidden');
            document.getElementById('no-packages')?.classList.add('hidden');
            const view = document.getElementById('hierarchy-view');
            view.classList.remove('hidden');

            // Header
            document.getElementById('h-name').textContent = data.tariffPackageDesc || tpName;

            const metaHTML = `
                <span class="meta-pill">${data.packageType}</span>
                <span class="meta-pill">${data.tariffPackCategory || 'NORMAL'}</span>
                <span class="meta-pill">${data.isCorporateYn ? 'Corporate' : 'Retail'}</span>
            `;
            document.getElementById('h-meta').innerHTML = metaHTML;

            const submittedBy = data.username || '—';
            const submittedOn = data.submittedOn || '—';
            document.getElementById('h-submeta').innerHTML = `
                <span>Submitted by <strong>${data.username || '—'}</strong></span>
                <span>${data.submittedOn || '—'}</span>
            `;

            // Main Plan
            document.getElementById('h-main-header').textContent =
                `📦 ${data.tariffPlanName || 'None'}`;

            // DATP Components
            const datp = data.defaultAtps || [];
            document.getElementById('h-datp-header').textContent = `🛒 DATP - ${datp.length} COMPONENTS`;
            const datpHtml = datp.map(item => `
                <div class="component-box">
                    <div class="comp-name">${item.packageName}</div>
                    <div class="comp-details">
                        <span class="pill"><strong>Validity:</strong> ${item.validity || '—'}</span>
                        <span class="pill"><strong>Midnight Expiry:</strong> ${item.midnightExpiry || '—'}</span>
                        <span class="pill"><strong>Renewal:</strong> ${item.renewal || '—'}</span>
                        <span class="pill"><strong>Rental:</strong> ${item.rental || '0'}</span>
                        <span class="pill"><strong>Max Count:</strong> ${item.maxCount || '0'}</span>
                        <span class="pill"><strong>Free Cycles:</strong> ${item.freeCycles || '0'}</span>
                    </div>
                </div>
            `).join('');

            document.getElementById('h-datp').innerHTML = datpHtml || '<p style="color:#94a3b8; font-size:13px; padding:8px 0;">No DATP components</p>';

            // AATP Components
            const aatp = data.additionalAtps || [];
            document.getElementById('h-aatp-header').textContent = `➕ AATP - ${aatp.length} COMPONENTS`;
            const aatpHtml = aatp.map(item => `
                <div class="component-box">
                    <div class="comp-name">${item.packageName}</div>
                    <div class="comp-details">
                        <span class="pill"><strong>Validity:</strong> ${item.validity || '—'}</span>
                        <span class="pill"><strong>Midnight Expiry:</strong> ${item.midnightExpiry || '—'}</span>
                        <span class="pill"><strong>Renewal:</strong> ${item.renewal || '—'}</span>
                        <span class="pill"><strong>Rental:</strong> ${item.rental || '0'}</span>
                        <span class="pill"><strong>Max Count:</strong> ${item.maxCount || '0'}</span>
                        <span class="pill"><strong>Free Cycles:</strong> ${item.freeCycles || '0'}</span>
                    </div>
                </div>
            `).join('');

            document.getElementById('h-aatp').innerHTML = aatpHtml || '<p style="color:#94a3b8; font-size:13px; padding:8px 0;">No AATP components</p>';

            // Footer
            document.getElementById('h-footer-bar').innerHTML = `
                <div><b>Charge:</b> RM ${data.charge}</div>
                <div><b>Ends:</b> ${data.endDate}</div>
            `;
        })
        .catch(err => {
            console.error(err);
            alert("Could not load hierarchy");
        });
}

// // Approve / Reject
// function approvePackage(tpName, btn) {
//     handleAction(tpName, 'A', btn);
// }

// function rejectPackage(tpName, btn) {
//     handleAction(tpName, 'R', btn);
// }

// function handleAction(tariffPackageId, status, btn) {
//     fetch('/admin/updateStatus', {
//         method: 'POST',
//         headers: { 'Content-Type': 'application/json' },
//         body: JSON.stringify({ tariffPackageId, status })
//     })
//         .then(res => {
//             if (res.ok) {
//                 const card = btn.closest('.approval-card');
//                 if (card) card.remove();

//                 alert(status === 'A' ? '✅ Package Approved' : '❌ Package Rejected');

//                 // Clear right panel if no cards left or selected one is gone
//                 if (document.querySelectorAll('.approval-card').length === 0) {
//                     document.getElementById('hierarchy-view').classList.add('hidden');
//                     document.getElementById('no-selection').classList.remove('hidden');
//                 }
//             } else {
//                 alert('Action failed');
//             }
//         })
//         .catch(() => alert('Server error'));
// }