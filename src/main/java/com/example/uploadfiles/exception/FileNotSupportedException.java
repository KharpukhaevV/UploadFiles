package com.example.uploadfiles.exception;

public class FileNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileNotSupportedException(String message) {
		super(message);
	}
}
