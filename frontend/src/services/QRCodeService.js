
class QRCodeService {

  generateQRCodeValue(tableId, baseUrl = window.location.origin) {

    const menuUrl = `${baseUrl}/menu/${tableId}`;
    return menuUrl;
  }
}

export default new QRCodeService();
