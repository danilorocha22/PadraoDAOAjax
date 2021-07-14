package com.danilorocha.padraodaoajax.DAO;

import com.danilorocha.padraodaoajax.Models.Pessoa;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Danilo Rocha
 */
public class PessoaDaoClasseXml implements PessoaDaoInterface{
    
    private Document doc;
    private String path;
    private int cod;
    
    public  PessoaDaoClasseXml(String path) throws ErroDaoException {
        try {
            DocumentBuilderFactory fab = DocumentBuilderFactory.newInstance();
            DocumentBuilder constr = fab.newDocumentBuilder();
            doc = constr.parse(path);
            this.path = path;
            geraCodigo();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new ErroDaoException(ex);
        }
    }
    
    private void geraCodigo() {
        NodeList pessoas = doc.getDocumentElement().getElementsByTagName("pessoa");
        int qt = pessoas.getLength();
        Element ultimo = (Element)pessoas.item(qt-1);
        cod = 1 + Integer.parseInt(ultimo.getElementsByTagName("codigo").item(0).getFirstChild().getNodeValue());
    }
    
    @Override
    public void criaPessoa(Pessoa p) throws ErroDaoException {
        p.setCodigo(cod);
        Element pessoa = doc.createElement("pessoa");
        Element codigo = doc.createElement("codigo");
        Element nome = doc.createElement("nome");
        Element idade = doc.createElement("idade");
        pessoa.appendChild(codigo);
        pessoa.appendChild(nome);
        pessoa.appendChild(idade);
        codigo.appendChild(doc.createTextNode(String.valueOf(p.getCodigo())));
        nome.appendChild(doc.createTextNode(p.getNome()));
        idade.appendChild(doc.createTextNode(String.valueOf(p.getIdade())));
        doc.getDocumentElement().appendChild(pessoa);
        salvar();
        cod++;
    }
    
    private Pessoa passaNoParaPessoa(Element no) {
        Pessoa p = new Pessoa();
        int cod = Integer.parseInt(no.getElementsByTagName("codigo").item(0).getFirstChild().getNodeValue());
        String nome = no.getElementsByTagName("nome").item(0).getFirstChild().getNodeValue();
        int idade = Integer.parseInt(no.getElementsByTagName("idade").item(0).getFirstChild().getNodeValue());
        p.setCodigo(cod);
        p.setNome(nome);
        p.setIdade(idade);
        return p;
    }

    @Override
    public List<Pessoa> pegaPessoas() throws ErroDaoException {
        List<Pessoa> pessoas = new ArrayList<>();
        NodeList listPessoas = doc.getDocumentElement().getElementsByTagName("pessoa");
        int qt = listPessoas.getLength();
        for(int i = 0; i < qt; i++) {
            Pessoa p = passaNoParaPessoa((Element)listPessoas.item(i));
            pessoas.add(p);
        }
        return pessoas;
    }
    
    private void salvar() throws ErroDaoException {
         try {
            TransformerFactory fab = TransformerFactory.newInstance();
            Transformer transf = fab.newTransformer();
            DOMSource fonte = new DOMSource(doc);
            File file = new File(path);
            StreamResult saida = new StreamResult(file);
            transf.transform(fonte, saida);
        } catch (TransformerException ex) {
            throw new ErroDaoException(ex);
        }
    }
    
    @Override
    public void sair() throws ErroDaoException {
        
    }
    
    public String serializar(Node no) throws ErroDaoException { 
        try {
            TransformerFactory fab = TransformerFactory.newInstance();
            Transformer transf = fab.newTransformer();
            DOMSource fonte = new DOMSource(no);
            ByteArrayOutputStream fluxo = new ByteArrayOutputStream();
            StreamResult saida = new StreamResult(fluxo);
            transf.transform(fonte, saida);
            return fluxo.toString();
        } catch (TransformerException ex) {
            throw new ErroDaoException(ex);
        }
    }
    
    
    @Override
    public Pessoa pegaPessoa(int cod) throws ErroDaoException {
        Pessoa p = null;
        NodeList listCod = doc.getDocumentElement().getElementsByTagName("codigo");
        int qt = listCod.getLength();
        for(int i = 0; i < qt; i++){
        Node noCod = listCod.item(i);
        String codTxt = noCod.getFirstChild().getNodeValue();
        int cod2 = Integer.parseInt(codTxt);
            if (cod2 == cod) {
                p = passaNoParaPessoa((Element)noCod.getParentNode());                
            }
        }
        return p;
    }

    @Override
    public void deletarPessoa(int cod) throws ErroDaoException {
        NodeList listCod = doc.getDocumentElement().getElementsByTagName("codigo");
        int qt = listCod.getLength();
        for(int i = 0; i < qt; i++){
        Node noCod = listCod.item(i);
        String codTxt = noCod.getFirstChild().getNodeValue();
        int cod2 = Integer.parseInt(codTxt);
            if (cod2 == cod) {
               Node noP = noCod.getParentNode();              
               noP.getParentNode().removeChild(noP);
               salvar();
            }
        }
        serializar(doc);
    }

    @Override
    public void deletarPessoa(Pessoa p) throws ErroDaoException {
        deletarPessoa(p.getCodigo());
    }

    @Override
    public void editarPessoa(Pessoa p) throws ErroDaoException {
        String pCodTxt = String.valueOf(p.getCodigo());
        NodeList listCod = doc.getDocumentElement().getElementsByTagName("codigo");
        int qt = listCod.getLength();
        for(int i = 0; i < qt; i++){
            Node noCod = listCod.item(i);
            String codTxt = noCod.getFirstChild().getNodeValue();
            if (codTxt.equals(pCodTxt)) {
               Element noP = (Element)noCod.getParentNode();
               noP.getElementsByTagName("nome").item(0).getFirstChild().setNodeValue(p.getNome());
               noP.getElementsByTagName("idade").item(0).getFirstChild().setNodeValue(String.valueOf(p.getIdade()));
               salvar();
            }
        }
        serializar(doc);
    }    
    
}
