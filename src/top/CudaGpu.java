package top;
import static jcuda.driver.JCudaDriver.*;
import jcuda.*;
import jcuda.driver.*;
import jcuda.runtime.JCuda;

public class CudaGpu {
	
	public CudaGpu() {
		cuInit(0);
	     CUcontext pctx = new CUcontext();
	     CUdevice dev = new CUdevice();
	     cuDeviceGet(dev, 0);
	     cuCtxCreate(pctx, 0, dev);
	     
	}
	
	public float[] getAcc(SpaceObject body, SpaceObject body2, float gravity) {
		
		
		
	 float[] mass = new float[] {(float)body.mass};
	 float[] mass2 = new float[] {(float)body2.mass};
	 float[] bodyX = new float[] {(float)body.getCenterX()};
	 float[] bodyY = new float[] {(float)body.getCenterY()};
	 float[] body2X = new float[] {(float)body2.getCenterX()};
	 float[] body2Y = new float[] {(float)body2.getCenterY()};
	 float[] g = new float[] {(float)gravity};
	 float[] dx = new float[1];
	 float[] dy = new float[1];
	  
     
     CUmodule module = new CUmodule();
     cuModuleLoad(module, "acceleration.ptx");
     CUfunction function = new CUfunction();
     cuModuleGetFunction(function, module, "getAcceleration");

     CUdeviceptr a_dev = new CUdeviceptr();
     cuMemAlloc(a_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(a_dev, Pointer.to(mass), Sizeof.FLOAT);
     
     CUdeviceptr b_dev = new CUdeviceptr();
     cuMemAlloc(b_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(b_dev, Pointer.to(mass2), Sizeof.FLOAT);
     
     CUdeviceptr c_dev = new CUdeviceptr();
     cuMemAlloc(c_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(c_dev, Pointer.to(bodyX), Sizeof.FLOAT);
     
     CUdeviceptr d_dev = new CUdeviceptr();
     cuMemAlloc(d_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(d_dev, Pointer.to(bodyY), Sizeof.FLOAT);
     
     CUdeviceptr e_dev = new CUdeviceptr();
     cuMemAlloc(e_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(e_dev, Pointer.to(body2X), Sizeof.FLOAT);
     
     CUdeviceptr f_dev = new CUdeviceptr();
     cuMemAlloc(f_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(f_dev, Pointer.to(body2Y), Sizeof.FLOAT);
     
     CUdeviceptr g_dev = new CUdeviceptr();
     cuMemAlloc(g_dev, Sizeof.FLOAT);
     cuMemcpyHtoD(g_dev, Pointer.to(g), Sizeof.FLOAT);

     CUdeviceptr h_dev = new CUdeviceptr();
     cuMemAlloc(h_dev, Sizeof.FLOAT);
     
     CUdeviceptr i_dev = new CUdeviceptr();
     cuMemAlloc(i_dev, Sizeof.FLOAT);


     Pointer kernelParameters = Pointer.to(
                                Pointer.to(a_dev),
                                Pointer.to(b_dev),
                                Pointer.to(c_dev),
                                Pointer.to(d_dev),
                                Pointer.to(e_dev),
                                Pointer.to(f_dev),
                                Pointer.to(g_dev),
                                Pointer.to(h_dev),
                                Pointer.to(h_dev)
                                );

     cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, null, kernelParameters, null);
     cuMemcpyDtoH(Pointer.to(dx), g_dev, Sizeof.FLOAT);
     cuMemcpyDtoH(Pointer.to(dy), h_dev, Sizeof.FLOAT);
     JCuda.cudaFree(a_dev);
     JCuda.cudaFree(b_dev);
     JCuda.cudaFree(c_dev);
     JCuda.cudaFree(d_dev);
     JCuda.cudaFree(e_dev);
     JCuda.cudaFree(f_dev);
     JCuda.cudaFree(g_dev);
     JCuda.cudaFree(h_dev);
     JCuda.cudaFree(i_dev);

     float[] values = new float[2];
     values[0] = dx[0];
     values[1] = dy[0];
     return values;
  }
}
