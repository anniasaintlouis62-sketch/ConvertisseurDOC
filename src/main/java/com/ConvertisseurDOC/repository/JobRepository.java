/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ConvertisseurDOC.repository;

import com.ConvertisseurDOC.model.ConversionJob;

/**
 *
 * @author annia
 */
public interface JobRepository {
     void save(ConversionJob job);

    ConversionJob findById(String id);

    boolean existsById(String id);

   void deleteById(String id);

    
}
