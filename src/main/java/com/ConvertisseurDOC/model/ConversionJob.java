/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ConvertisseurDOC.model;

/**
 *
 * @author annia
 */


import java.time.Instant;

public class ConversionJob {
    private final String id;
    private final ConversionType type;
    private JobStatus status;
    private final Instant createdAt;

    private String inputFileName;
    private String outputFileName;
    private String previewFileName;
    private String errorMessage;

    public ConversionJob(String id, ConversionType type) {
        this.id = id;
        this.type = type;
        this.status = JobStatus.UPLOADED;
        this.createdAt = Instant.now();
    }
    public String getInputFileName(){ return inputFileName;}

    public String getId() { return id; }
    public ConversionType getType() { return type; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
   
    public Instant getCreatedAt() { return createdAt; }

   
    public void setInputFileName(String inputFileName) { this.inputFileName = inputFileName; }

    public String getOutputFileName() { return outputFileName; }
    public void setOutputFileName(String outputFileName) { this.outputFileName = outputFileName; }

    public String getPreviewFileName() { return previewFileName; }
    public void setPreviewFileName(String previewFileName) { this.previewFileName = previewFileName; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}