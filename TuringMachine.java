package turing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * チューリングマシン
 */
public class TuringMachine{

    /** 現在の状態 */
    private TuringState curState;

    /** テープ */
    private ArrayList<TuringTape> tapes;

    /** 動作表 */
    private HashMap<TuringCurrent, TuringNext> map;

    /**
     * 1ステップ状態遷移を行う
     */
    public void step(){
        // 各ヘッドから記号を読み取る
        TuringSymbol[] symbols = new TuringSymbol[k];
        for(TuringTape tape : tapes){
            symbols[i] = tape.getSymbol();
        }
        // 動作表に基づいて遷移する
        TuringNext next = map.get(new TuringCurrent(curState, symbols));
        
        for(TuringTape tape : tapes){
            tape.putSymbol(next.symbols[i]);
            tape.moveHead(next.tds[i]);
        }

        curState = next.state;
    }

    /**
     * コンストラクタ
     * @param s 初期状態
     * @param tapes テープの集合
     * @param map 動作表
     */
    TuringMachine(TuringState s, ArrayList<TuringSymbol> tapes, HashMap<TuringCurrent, TuringNext> map)
    {
        this.curState = s;
        this.map = map;
        this.tapes = new ArrayList<TuringTape>();
        for (int i=0; i<k; i++) tapes[i] = new TuringTape(inputs[i]);
    }

    @Override
    public String toString(){
        String str = "curState:" + curState + ", tapes:[\n";
        for (int i=0; i<k; i++){
            str += tapes[i] + "\n";
        }
        str += "]";

        return str;
    }

    /**
     * テスト用main
     */
    public static void main(String[] args){
        HashMap<Integer,TuringSymbol>[] tapes = new HashMap<Integer,TuringSymbol>[2];
        tapes[0] = new HashMap<Integer,TuringSymbol>();
        tapes[1] = new HashMap<Integer,TuringSymbol>();
        tapes[0].put(0,"1");
        tapes[1].put(0,"1");

        TuringSymbol[] ss00 = {symbol0,symbol0};
        TuringSymbol[] ss01 = {symbol0,symbol1};
        TuringSymbol[] ss10 = {symbol1,symbol0};
        TuringSymbol[] ss11 = {symbol1,symbol1};
        
        TuringDirection[] dd = {TuringDirection.DONT_MOVE, TuringDirection.DONT_MOVE};
        TuringDirection[] ld = {TuringDirection.LEFT, TuringDirection.DONT_MOVE};

        HashMap<TuringCurrent, TuringNext> map = new HashMap<TuringCurrent, TuringNext>();
        map.put(new TuringCurrent(s1,ss00), new TuringNext(s3,ss00,dd));
        map.put(new TuringCurrent(s1,ss01), new TuringNext(s3,ss11,dd));
        map.put(new TuringCurrent(s1,ss10), new TuringNext(s3,ss10,dd));
        map.put(new TuringCurrent(s1,ss11), new TuringNext(s2,ss01,ld));
        map.put(new TuringCurrent(s2,ss00), new TuringNext(s3,ss11,dd));
        map.put(new TuringCurrent(s2,ss01), new TuringNext(s3,ss11,dd));
        map.put(new TuringCurrent(s2,ss10), new TuringNext(s3,ss11,dd));
        map.put(new TuringCurrent(s2,ss11), new TuringNext(s3,ss11,dd));
        
        TuringMachine tm = new TuringMachine(q, s1, gamma, tapes, map);
        System.out.println(tm);
    }
}

/**
 * チューリング機械の状態
 */
class TuringState{
    String name = null;

    TuringState(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof TuringState){
            TuringState s = (TuringState) obj;
            if (this.name.equals(s.name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    @Override
    public String toString(){
        return name;
    }
}

/**
 * チューリング機械のテープ記号
 */
class TuringSymbol{
    static final TuringSymbol SPACE = new TuringSymbol(null);

    String name;

    TuringSymbol(String name){
        this.name = name;
    }

    /**
     * カンマ区切りで与えられた記号列をTuringSymbolの配列に変換する。
     * 入力は"0,1,1,0,1"のように与える。
     * @return TuringSymbolの配列
     */
    public static TuringSymbol[] toSymbols(String str){
        String[] strs = str.split(",", 0);
        TuringSymbol[] ret = TuringSymbol[strs.length];
        for (int i=0; i<ret.length; i++){
            ret[i] = new TuringSymbol(strs[i]);
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof TuringSymbol){
            TuringSymbol s = (TuringSymbol) obj;
            if (this.name.equals(s.name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}

/**
 * チューリング機械の現在状況
 * （状態と、k個の記号の組）
 */
final class TuringCurrent{
    private TuringState state;
    private TuringSymbol[] symbols;
    
    /**
     * コンストラクタ
     * @param state 現在の状態
     * @param symbols 現在のヘッド位置にある記号
     */
    TuringCurrent(TuringState state, TuringSymbol[] symbols){
        this.state = state;
        this.symbols = symbols;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof TuringCurrent){
            TuringCurrent tc = (TuringCurrent) obj;
            if (this.state.equals(tc.state) && this.symbols.length == tc.symbols.length){
                for (int i=0; i<symbols.length; i++){
                    if (!this.symbols[i].equals(tc.symbols[i])){
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(state,symbols);
    }
}

/**
 * チューリング機械の次状況
 * （状態と、k個の記号と方向の組）
 */
class TuringNext{
    protected TuringState state;
    protected TuringSymbol[] symbols;
    protected TuringDirection[] tds;
    
    /**
     * コンストラクタ
     * @param state 次の状態
     * @param symbols 現在のヘッド位置に書く記号
     * @param tds ヘッドの移動方向
     */
    TuringNext(TuringState state, TuringSymbol[] symbols, TuringDirection[] tds){
        this.state = state;
        this.symbols = symbols;
        this.tds = tds;
    }
}

/**
 * チューリング機械のテープ
 */
class TuringTape{

    /**
     * テープ上の位置と、そこに書かれている記号のHashMap
     */
    HashMap<Integer,TuringSymbol> tape = null;

    /**
     * ヘッドの位置
     */
    int head = 0;

    /**
     * コンストラクタ
     * @param input 入力記号列
     */
    TuringTape(TuringSymbol[] input){
        tape = new HashMap<Integer,TuringSymbol>();
        for (int i=0; i<input.length; i++){
            tape.put(i, input[i]);
        }
    }

    /**
     * コンストラクタ
     * @param tape テープ
     */
    TuringTape(HashMap<Integer,TuringSymbol> tape){
        this.tape = tape;
    }

    /**
     * ヘッドを動かす
     * @param td ヘッドを動かす方向
     */
    public void moveHead(TuringDirection td){
        if (td == TuringDirection.LEFT){
            head--;
        } else if (td == TuringDirection.RIGHT){
            head++;
        }
    }

    /**
     * ヘッド位置の記号を読み出す
     * @return ヘッド位置の記号
     */
    public TuringSymbol getSymbol(){
        return tape.get(head);
    }

    /**
     * ヘッド位置に記号を書き込む
     * @param symbol 書きこむ記号
     */
    public void putSymbol(TuringSymbol symbol){
        tape.put(head, symbol);
    }

    @Override
    public String toString(){
        int max = getMaxkey();
        int min = getMinkey();
        String str = "[";

        for (int i=min; i<=max; i++){
            str += tape.get(i);
            if (i==head) str += "*";
            if (i==max) break;
            str += ", ";
        }
        str += "]";
        return str;
    }

    private int getMaxkey(){
        int max = Integer.MIN_VALUE;
        for (Integer key : tape.keySet()){
            if (key>max) max = key;
        }
        return max;
    }

    private int getMinkey(){
        int min = Integer.MAX_VALUE;
        for (Integer key : tape.keySet()){
            if (key<min) min = key;
        }
        return min;
    }
}

/**
 * チューリング機械の進む方向
 */
enum TuringDirection{
    LEFT, RIGHT, DONT_MOVE
}