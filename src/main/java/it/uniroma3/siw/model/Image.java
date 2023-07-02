package it.uniroma3.siw.model;

import java.util.Base64;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String b64Image;


    
    public Image() {
    }

  
    public Image(byte[] bytes) {
        this.setB64Image(Base64.getEncoder().encodeToString(bytes));
    }

    public String getB64Image() {
        return b64Image;
    }

    public void setB64Image(String b64Image) {
        this.b64Image = b64Image;
    }
    
}
