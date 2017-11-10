package logic;

import java.util.ArrayList;

public class Survey {

	private int survey_id;
	private int opleiding_id;
	private ArrayList<Vraag> vragen = new ArrayList<Vraag>();

	public Survey(int survey_id, int opleiding_id) {
		this.survey_id = survey_id;
		this.opleiding_id = opleiding_id;
	}

	public int getSurvey_id() {
		return survey_id;
	}

	public void setSurvey_id(int survey_id) {
		this.survey_id = survey_id;
	}

	public int getOpleiding_id() {
		return opleiding_id;
	}

	public void setOpleiding_id(int opleiding_id) {
		this.opleiding_id = opleiding_id;
	}

	public ArrayList<Vraag> getVragen() {
		return vragen;
	}

	public void setVragen(ArrayList<Vraag> vragen) {
		this.vragen = vragen;
	}

	public String getEersteVraag() {
		return vragen.get(0).getVraag();
	}

	public void addVraag(Vraag v) {

		if (v != null) {

			// Return indien vraag al bestaat
			for (Vraag vraag : vragen) {
				if (vraag.equals(v))
					return;
			}
			vragen.add(v);
		}
	}

	public boolean deleteVraag(int index) {
		if (index >= 0 && index < vragen.size()) {
			vragen.remove(index);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Survey [survey_id=" + survey_id + ", opleiding_id=" + opleiding_id + ", vragen=" + vragen + "]";
	}
}