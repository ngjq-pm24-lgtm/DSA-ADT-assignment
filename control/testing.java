/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import ADT.MapInterface;
import Entity.Consultation;
import dao.ConsultationDAO;

/**
 *
 * @author ACER
 */
public class testing {
    
    public static void main(String[] args){
        ConsultationDAO consultationDAO = new ConsultationDAO();

        MapInterface<String, Consultation> loadedConsultations = (MapInterface<String, Consultation>) consultationDAO.loadConsultations();
        loadedConsultations.printDebugTable();
    }
    
}
