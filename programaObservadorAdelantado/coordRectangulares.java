public class coordRectangulares extends coordenadas {
    private double coordX; // longitud
    private double coordY; // latitud
    private double cotaXY; // opcional, no usada en distancia sobre superficie

    public coordRectangulares(double x, double y, double cota) {
        this.coordX = x;
        this.coordY = y;
        this.cotaXY = cota;
    }

    public double getX() { return coordX; }
    public double getY() { return coordY; }
    public double getCota() { return cotaXY; }

    @Override
    public double distanciaA(coordenadas otro) {
        double lat1 = this.coordY;
        double lon1 = this.coordX;
        double lat2, lon2;

        if (otro instanceof coordRectangulares) {
            coordRectangulares c = (coordRectangulares) otro;
            lat2 = c.getY();
            lon2 = c.getX();
        } else if (otro instanceof coordPolares) {
            coordPolares c = (coordPolares) otro;
            coordRectangulares p2 = c.toRectangulares(); // convertir polar -> lat/lon
            lat2 = p2.getY();
            lon2 = p2.getX();
        } else {
            throw new IllegalArgumentException("Tipo de coordenada no soportado");
        }

        return distanciaVincenty(lat1, lon1, lat2, lon2);
    }

    // Vincenty para medición precisa (WGS84)
    public double distanciaVincenty(double lat1, double lon1, double lat2, double lon2) {
        double a = 6378137.0;
        double f = 1 / 298.257223563;
        double b = (1 - f) * a;

        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double L = Math.toRadians(lon2 - lon1);
        double Lambda = L;

        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma;
        double sinAlpha, cos2Alpha, cos2SigmaM, C;
        int iterLimit = 100;
        double LambdaPrev;

        do {
            sinLambda = Math.sin(Lambda);
            cosLambda = Math.cos(Lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                                 (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) *
                                 (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) return 0; // coinciden
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cos2Alpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cos2Alpha != 0 ? cosSigma - 2 * sinU1 * sinU2 / cos2Alpha : 0;
            C = f / 16 * cos2Alpha * (4 + f * (4 - 3 * cos2Alpha));
            LambdaPrev = Lambda;
            Lambda = L + (1 - C) * f * sinAlpha *
                     (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(Lambda - LambdaPrev) > 1e-12 && --iterLimit > 0);

        double uSquared = cos2Alpha * (a * a - b * b) / (b * b);
        double A = 1 + uSquared / 16384 * (4096 + uSquared * (-768 + uSquared * (320 - 175 * uSquared)));
        double B = uSquared / 1024 * (256 + uSquared * (-128 + uSquared * (74 - 47 * uSquared)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 *
                          (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
                           B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) *
                           (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        return b * A * (sigma - deltaSigma);
    }
}