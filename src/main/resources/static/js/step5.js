window.addEventListener('DOMContentLoaded', () => {
    // Restore previously entered values
    const state = getState();
    if (state.price) document.getElementById('price').value = state.price;
    if (state.endDate) document.getElementById('endDate').value = state.endDate;
    if (state.publicityCode) document.getElementById('publicityCode').value = state.publicityCode;
    if (state.isCorporate) document.getElementById('isCorporate').checked = state.isCorporate;

    // ── Clone mode: swap Save Config → Clone Package ──────────────
    if (sessionStorage.getItem('cloneMode') === 'true') {
        const saveBtn = document.getElementById('saveConfigBtn');
        const cloneBtn = document.getElementById('clonePackageBtn');
        if (saveBtn) saveBtn.style.display = 'none';
        if (cloneBtn) cloneBtn.style.display = 'inline-flex';
    }
});

// Each input writes to state immediately on change
function onPriceChange(val) {
    const state = getState();
    state.price = val;
    saveState(state);
}

function onEndDateChange(val) {
    const state = getState();
    state.endDate = val;
    saveState(state);
}

function onPublicityCodeChange(val) {
    const state = getState();
    state.publicityCode = val;
    saveState(state);
}

function onCorporateChange(checked) {
    const state = getState();
    state.isCorporate = checked;
    saveState(state);
}

// ── Clone Package — builds payload entirely from sessionStorage ──
async function clonePackageFromBuilder() {
    const state = getState();

    if (!state?.s2?.length) {
        alert('Service Plan selection in Step 2 is required');
        return;
    }
    if (!state.price) {
        alert('Enter charge amount');
        return;
    }
    if (!state.endDate) {
        alert('Select end date');
        return;
    }
    if (!state.publicityCode) {
        alert('Enter publicity code');
        return;
    }

    const originalTpName = sessionStorage.getItem('cloneTpName');
    const networkId = sessionStorage.getItem('cloneNetworkId');
    const username = sessionStorage.getItem('cloneUsername');

    if (!originalTpName || !networkId) {
        alert('Clone context missing. Please go back to the Clone page and try again.');
        return;
    }

    function formatDateToMMDDYYYY(dateStr) {
        if (!dateStr) return '12/31/2030';
        const [year, month, day] = dateStr.split('-');
        return `${month}/${day}/${year}`;
    }

    const chargeId = originalTpName + '_PR';

    const payload = {
        tpName: originalTpName,
        networkId: Number(networkId),
        username: username || USERNAME,
        data: {
            tariffPackageDesc: originalTpName,
            packageType: sessionStorage.getItem('pkgType') || '',
            tariffPackCategory: sessionStorage.getItem('pkgSubType') || 'NORMAL',
            charge: state.price,
            endDate: formatDateToMMDDYYYY(state.endDate),
            publicityId: state.publicityCode,
            chargeId: chargeId,
            isCorporateYn: state.isCorporate || false,
            tariffPlanId: Number(state.s2[0].id),
            tariffPlanName: state.s2[0].name,
            selectedSvcs_s2: sessionStorage.getItem('selectedSvcs_s2') || '[]',
            selectedSvcs_s3: sessionStorage.getItem('selectedSvcs_s3') || '[]',
            selectedSvcs_s4: sessionStorage.getItem('selectedSvcs_s4') || '[]',
            defaultAtps: (state.s3 || []).map(item => ({
                servicePackageId: Number(item.id),
                chargeId: chargeId,
                packageName: item.name,
                validity: item.validity,
                rentalPeriod: item.validity === 'O' ? (item.rentalPeriod || 1) : "",
                midnightExpiry: item.midnightExpiry,
                renewal: item.renewal,
                rental: item.rental || 0,
                maxCount: item.maxCount || 0,
                freeCycles: item.freeCycles || 0
            })),
            allowedAtps: (state.s4 || []).map(item => ({
                servicePackageId: Number(item.id),
                chargeId: chargeId,
                packageName: item.name,
                validity: item.validity,
                rentalPeriod: item.validity === 'O' ? (item.rentalPeriod || 1) : "",
                midnightExpiry: item.midnightExpiry,
                renewal: item.renewal,
                rental: item.rental || 0,
                maxCount: item.maxCount || 0,
                freeCycles: item.freeCycles || 0
            }))
        }
    };

    const cloneBtn = document.getElementById('clonePackageBtn');
    if (cloneBtn) { cloneBtn.disabled = true; cloneBtn.textContent = 'Cloning…'; }

    try {
        const res = await fetch('/clone', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await res.json();

        if (!res.ok || result.error) {
            alert(result.error || 'Clone failed. Please try again.');
            return;
        }

        alert('✅ Cloned successfully!\nNew plan: ' + result.clonedTpName);

        // Clean up clone-specific session keys
        sessionStorage.removeItem('cloneMode');
        sessionStorage.removeItem('cloneTpName');
        sessionStorage.removeItem('cloneNetworkId');
        sessionStorage.removeItem('cloneUsername');
        clearBuilderSession();

        window.isInternalNavigation = true;
        window.location.href = '/builder/step1';

    } catch (err) {
        console.error('Clone error:', err);
        alert('Server error during clone. Please try again.');
    } finally {
        if (cloneBtn) { cloneBtn.disabled = false; cloneBtn.textContent = 'CLONE PACKAGE'; }
    }
}