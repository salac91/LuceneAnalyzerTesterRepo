package tools.models;

public class AnalysisResultModel {

	private float precision;
	
	private float retrieval;
	
	private float correctness;
	
	private float fmera;

	public float getPrecision() {
		return precision;
	}

	public void setPrecision(float precision) {
		this.precision = precision;
	}

	public float getRetrieval() {
		return retrieval;
	}

	public void setRetrieval(float retrieval) {
		this.retrieval = retrieval;
	}

	public float getCorrectness() {
		return correctness;
	}

	public void setCorrectness(float correctness) {
		this.correctness = correctness;
	}

	public float getFmera() {
		return fmera;
	}

	public void setFmera(float fmera) {
		this.fmera = fmera;
	}

}
