#include <math.h>

extern "C"
__global__ void getAcceleration(float *bodyX, float *bodyY, float *body2X, float *body2Y, float *mass, float *mass2, float *g, float *dx, float *dy)
                    /*** kernel code ***/
{
    float d = sqrt(pow (bodyX[0] - body2X[0], 2) + pow(bodyY[0] - body2Y[0], 2));
    dx[0] = g[0] * mass[0] / (d*d) * (bodyX[0] - body2X[0]) / d;
    dy[0] = g[0] * mass[0] / (d*d) * (bodyY[0] - body2Y[0]) / d;
}
