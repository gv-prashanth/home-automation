package com.vadrin.homeautomation.models.alexa;

public class Response {
	private String message;
	private boolean theEnd;

	public String getMessage() {
		return message;
	}

	public boolean isTheEnd() {
		return theEnd;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTheEnd(boolean theEnd) {
		this.theEnd = theEnd;
	}

	public Response(String message, boolean theEnd) {
		super();
		this.message = message;
		this.theEnd = theEnd;
	}

	public Response() {
		super();
	}

}
