/* Generated By:JavaCC: Do not edit this line. BWParser.java */
package org.glite.authz.pap.encoder;
import java.lang.String;
import org.glite.authz.pap.ui.wizard.*;
import org.opensaml.xacml.XACMLObject;
import java.util.List;

class BWParser implements BWParserConstants {

  static final public List<XACMLObject> Text() throws ParseException {
  Policies policies = null;
  Policies allpolicies = new Policies();
  String output = new String();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RESOURCE:
      case CLASS:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      policies = SectionType();
    allpolicies.policies.addAll(policies.policies);
    }
    {if (true) return allpolicies.Output();}
    throw new Error("Missing return statement in function");
  }

  static final public Policies Section() throws ParseException {
  Policies policies=new Policies();
  Policy policy=null;
    label_2:
    while (true) {
      policy = SectionType();
     policies.policies.add(policy);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RESOURCE:
      case CLASS:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
    }
    {if (true) return policies;}
    throw new Error("Missing return statement in function");
  }

  static final public Policy SectionType() throws ParseException {
  Policy policy = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RESOURCE:
      jj_consume_token(RESOURCE);
      policy = BW_Policy();
    {if (true) return policy;}
      break;
    case CLASS:
      jj_consume_token(CLASS);
      policy = ServiceClassPolicy();
    {if (true) return policy;}
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Policies BW_Policies() throws ParseException {
  Policy policy = null;
  Policies policies = new Policies();
    label_3:
    while (true) {
      jj_consume_token(RESOURCE);
      policy = BW_Policy();
                                  policies.policies.add(policy);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RESOURCE:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_3;
      }
    }
    {if (true) return policies;}
    throw new Error("Missing return statement in function");
  }

  static final public Policy BW_Policy() throws ParseException {
  String resource=null;
  Conds conds = null;
  Policy policy = new Policy();
    resource = TextString();
    jj_consume_token(26);
    conds = Conditions();
    jj_consume_token(27);
    policy.name = resource;
    policy.type = Policy.POLICY_TYPE_BW;
    policy.conds = conds;
    policy.policies = null;
    {if (true) return policy;}
    throw new Error("Missing return statement in function");
  }

  static final public Policies ServiceClassPolicies() throws ParseException {
  Policy policy = null;
  Policies policies = new Policies();
    label_4:
    while (true) {
      policy = ServiceClassPolicy();
      policies.policies.add(policy);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING:
      case ID:
      case ID2:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_4;
      }
    }
    {if (true) return policies;}
    throw new Error("Missing return statement in function");
  }

  static final public Policy ServiceClassPolicy() throws ParseException {
  String resource=null;
  Conds conds = null;
  Policy policy = new Policy();
  Policies policies = null;
    resource = TextString();
    jj_consume_token(26);
    policies = BW_Policies();
    jj_consume_token(27);
    policy.name = resource;
    policy.type = Policy.POLICY_TYPE_CLASS;
    policy.conds = null;
    policy.policies = policies;
    {if (true) return policy;}
    throw new Error("Missing return statement in function");
  }

  static final public Conds Conditions() throws ParseException {
  FullCondition fc = null;
  Conds conds = new Conds();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ALLOW:
      case DENY:
      case PRIVATE:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_5;
      }
      fc = Condition();
                   conds.fullconditions.add(fc);
    }
    {if (true) return conds;}
    throw new Error("Missing return statement in function");
  }

  static final public FullCondition Condition() throws ParseException {
  FullCondition fc = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIVATE:
      jj_consume_token(PRIVATE);
      fc = PublicCondition();
    fc.isprivate = true;
    {if (true) return fc;}
      break;
    case ALLOW:
    case DENY:
      fc = PublicCondition();
    fc.isprivate = false;
    {if (true) return fc;}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public FullCondition PublicCondition() throws ParseException {
  boolean result = false;
  ConditionRow item = null;
  ConditionList excepts = new ConditionList();
  FullCondition fc = new FullCondition();
  ConditionRow except = null;
    result = AllowOrDeny();
    item = ItemsRow();
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EXCEPT:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_6;
      }
      except = Exception();
                                                               excepts.rows.add(except);
    }
    fc.allow = result;
    fc.simples = item;
    fc.excepts = excepts;
    {if (true) return fc;}
    throw new Error("Missing return statement in function");
  }

  static final public boolean AllowOrDeny() throws ParseException {
 boolean result;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DENY:
      jj_consume_token(DENY);
    {if (true) return false;}
      break;
    case ALLOW:
      jj_consume_token(ALLOW);
    {if (true) return true;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public ConditionRow Exception() throws ParseException {
  ConditionRow row = null;
    jj_consume_token(EXCEPT);
    row = ItemsRow();
    {if (true) return row;}
    throw new Error("Missing return statement in function");
  }

  static final public ConditionRow ItemsRow() throws ParseException {
  SingleCondition sc=null;
  ConditionRow row = new ConditionRow();
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FQAN:
      case DN:
      case RESOURCE:
      case CERT:
      case STRING:
      case ID:
      case ID2:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_7;
      }
      sc = Item();
    row.singles.add(sc);
    }
    {if (true) return row;}
    throw new Error("Missing return statement in function");
  }

  static final public SingleCondition Item() throws ParseException {
  boolean value = false;
  String s1 = null;
  String s2 = null;
  SingleCondition sc = new SingleCondition();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FQAN:
      jj_consume_token(FQAN);
      jj_consume_token(28);
      s1 = TextString();
    sc.type = SingleCondition.TYPE_FQAN;
    sc.value1 = s1;
    {if (true) return sc;}
      break;
    case DN:
      jj_consume_token(DN);
      jj_consume_token(28);
      s1 = TextString();
    sc.type = SingleCondition.TYPE_DN;
    sc.value1 = s1;
    {if (true) return sc;}
      break;
    case CERT:
      jj_consume_token(CERT);
      jj_consume_token(28);
      jj_consume_token(29);
      s1 = TextString();
      jj_consume_token(30);
      s2 = Number();
      jj_consume_token(31);
    sc.type = SingleCondition.TYPE_CERT;
    sc.value1 = s1;
    sc.value2 = s2;
    {if (true) return sc;}
      break;
    case RESOURCE:
      jj_consume_token(RESOURCE);
      jj_consume_token(28);
      s1 = TextString();
    sc.type = SingleCondition.TYPE_RESOURCE;
    sc.value1 = s1;
    {if (true) return sc;}
      break;
    case STRING:
    case ID:
    case ID2:
      s1 = TextString();
      jj_consume_token(28);
      s2 = TextString();
    sc.type = SingleCondition.TYPE_GA;
    sc.value1 = s1;
    sc.value2 = s2;
    {if (true) return sc;}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public boolean YesOrNo() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case YES:
      jj_consume_token(YES);
    {if (true) return true;}
      break;
    case NO:
      jj_consume_token(NO);
    {if (true) return false;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public String TextString() throws ParseException {
  Token t = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING:
      t = jj_consume_token(STRING);
    {if (true) return t.toString();}
      break;
    case ID:
      t = jj_consume_token(ID);
    {if (true) return t.toString();}
      break;
    case ID2:
      t = jj_consume_token(ID2);
    {if (true) return t.toString();}
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Number() throws ParseException {
  Token t = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NUMBER:
      t = jj_consume_token(NUMBER);
    {if (true) return t.toString();}
      break;
    case HEX:
      t = jj_consume_token(HEX);
    {if (true) return t.toString();}
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  static public BWParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  static public Token token, jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[14];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x40800,0x40800,0x40800,0x800,0xe00000,0x1000c0,0x1000c0,0x80000,0xc0,0xe01b00,0xe01b00,0x18000,0xe00000,0x3000000,};
   }

  public BWParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public BWParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new BWParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  public BWParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new BWParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  public BWParser(BWParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  public void ReInit(BWParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  static final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.Vector jj_expentries = new java.util.Vector();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  static public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[32];
    for (int i = 0; i < 32; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 14; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 32; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  static final public void enable_tracing() {
  }

  static final public void disable_tracing() {
  }

//     public static String main(String args[]) throws ParseException {
//         BWParser parser = new BWParser(System.in);
//         try {
//             return parser.Text();  
//         }
//         catch (ParseException e) {
//             System.out.println("EXCEPTION");
//             System.out.println(e);
//         }
//     }
}
