
/* Utility Functions for Host and Client */
import java.math.*;
import java.net.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;


class Utility {

	public Utility() {}


  // hash password using SHA-1 Alogorithm
  // IN REAL WORLD, DO NOT EVER USE SHA-1.
  private String sha1Hash (String pwd) {

    String hashed_pwd = "";

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");

      // convert String to bytes arry
      byte[] pwd_bytes = md.digest(pwd.getBytes());

      // convert byte array to signum value
      BigInteger signum_val = new BigInteger(1, pwd_bytes);

      hashed_pwd = signum_val.toString();

      // fill "0"s to get 64-bit length
      while ( hashed_pwd.length() < 64 )
        hashed_pwd = "0" + hashed_pwd;

    }
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return hashed_pwd;
  }


  // save password, P and G values to File
  private void saveToFile (List<String> lines) {

    try {
      Path filepath = Paths.get("secret.txt");
      Files.write(filepath, lines, StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  // generate G and P for Diffie-Hellman Alogorithm
  private BigInteger[] generatePandG () {
    Random rand = new Random();

    BigInteger P = BigInteger.probablePrime(1024, rand);
    BigInteger G = BigInteger.probablePrime(1024, rand);

    return new BigInteger[] {P, G};
  }


  /**
   *  Host Set up: generating P and G
   * saving password, P and G to file
   * @return void
   */
  public void hostSetUp () {
    BigInteger[] p_g = new BigInteger[2];

    p_g = this.generatePandG();

    BigInteger P = p_g[0];
    BigInteger G = p_g[1];

    String pwd = "Secret123";
    String hashed_pwd = this.sha1Hash(pwd);

    // save Password, G and P to file
    this.saveToFile(Arrays.asList(hashed_pwd, P.toString(), G.toString()));
  }


  /**
   *  read from file
   * @return List<String>
   */
  public List<String> readFromFile() {

    try {
      Path file = Paths.get("secret.txt");
      
      return Files.readAllLines(file);
    }
    catch (IOException ie) {
      throw new RuntimeException(ie);
    }
  
  }


  /** 
   * find mod value
   * @return BigInteger
   */
  public BigInteger findModulo(BigInteger P, BigInteger G, int m) {
    
    BigInteger ex = G.pow(m);

    return ex.mod(P);
  }


  /**
   * Generate Secret Key
   * @return SecretKey
   */
  public SecretKey generateSecretKey(String secret) throws Exception {
    
    byte[] secret_bytes = secret.getBytes();

    return new SecretKeySpec(secret_bytes, 0, secret_bytes.length, "ARCFOUR");
  }


  /**
   * Encrypt Message
   * @return byte[]
   */
  public byte[] encrypt(String str, SecretKey key, Cipher cipher) throws Exception {

    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] str_bytes = str.getBytes();
    
    return cipher.doFinal(str_bytes);
  }


  /**
   * Decrypt Message
   * @return String
   */
  public String decrypt(byte[] encrypted_bytes, SecretKey key, Cipher cipher) throws Exception {

    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decrypted_bytes = cipher.doFinal(encrypted_bytes);

    return new String(decrypted_bytes);
  }



}
