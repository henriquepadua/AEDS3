import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Jogador {
    public int Id;
    public int tamanhoRegistro;
    public char lapide;
    public String KnownAs;
    public String FullName;
    public int Overall;
    public int Potential;
    public int Value;
    public String PositionsPlayed;
    public String BestPosition;
    public String Nationality;
    public String ImageLink;

    public Jogador(){
        this.ImageLink = "";
        this.BestPosition = "";
        this.PositionsPlayed = "";
        this.Value = 0;
        this.Overall = 0;
        this.FullName = "";
        this.KnownAs = "";
        this.Id = -1;
        this.lapide = ' ';
        this.tamanhoRegistro = 0;
    }

    public Jogador(int id,String KnownAs,String FullName,int Overall,int Potential,int Value,String PositionsPlayed,String BestPosition
                ,String Nationality,String ImageLink,int tamanhoRegistro){
        this.ImageLink = ImageLink;
        this.BestPosition = BestPosition;
        this.PositionsPlayed = PositionsPlayed;
        this.Value = Value;
        this.Overall = Overall;
        this.FullName = FullName;
        this.KnownAs = KnownAs;
        this.Id = id;
        this.tamanhoRegistro = tamanhoRegistro;
    }

    public byte[] toByteArray() throws IOException{

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeChar(lapide);           
            dos.writeInt(Id);
            dos.writeInt(KnownAs.length());
            dos.writeUTF(KnownAs);           
            dos.writeInt(FullName.length());
            dos.writeUTF(FullName);
            dos.writeInt(Overall);
            dos.writeInt(Potential);
            dos.writeInt(Value);
            dos.writeInt(PositionsPlayed.length());
            dos.writeUTF(PositionsPlayed);
            dos.writeInt(BestPosition.length());
            dos.writeUTF(BestPosition);
            if(Nationality == null) Nationality = "";
            dos.writeInt(Nationality.length());
            dos.writeUTF(Nationality);
            dos.writeInt(ImageLink.length());
            dos.writeUTF(ImageLink);
            
            return baos.toByteArray();
    }

    @Override
    public String toString(){//metodo para verificar os dados da conta
      String mostrada = KnownAs + FullName + Overall + Potential + Value + PositionsPlayed + BestPosition + Nationality + ImageLink;

        return mostrada;
    }
}
