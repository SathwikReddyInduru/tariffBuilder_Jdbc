// window.onload = function () {
//     sessionStorage.removeItem('pkgType');
//     sessionStorage.removeItem('pkgSubType');
//     sessionStorage.removeItem('state');
//     sessionStorage.removeItem('selectedSvcs_s2');
//     sessionStorage.removeItem('selectedSvcs_s3');
//     sessionStorage.removeItem('selectedSvcs_s4');
//     sessionStorage.removeItem('configName');

//     const input = document.getElementById('configName');
//     if (input) input.value = '';
// };

function selectType(type) {
    sessionStorage.setItem('pkgType', type);
    document.getElementById('typeSection').classList.add('hidden');
    document.getElementById('subTypeSection').classList.remove('hidden');
    document.querySelector('.main-title').textContent = 'Select Category';
}

function selectSubType(subType) {
    sessionStorage.setItem('pkgSubType', subType);
    window.location.href = '/builder/step2';
}