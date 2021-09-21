package origami_editor.editor.export;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.CreasePattern_Worker;
import origami.crease_pattern.worker.HierarchyList_Worker;
import origami.folding.element.SubFace;
import origami_editor.editor.LineStyle;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.record.Memo;
import origami_editor.sortingbox.SortingBox;
import origami_editor.tools.Camera;
import origami_editor.tools.StringOp;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Svg {
    public static Memo exportFile(Memo mem_tenkaizu, Memo mem_oriagarizu) {
        System.out.println("svg画像出力");
        Memo MemR = new Memo();

        MemR.reset();

        MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

        MemR.addMemo(mem_tenkaizu);
        MemR.addMemo(mem_oriagarizu);

        MemR.addLine("</svg>");
        return MemR;
    }

    public static Memo getMemo_wirediagram_for_svg_export(HierarchyList_Worker ctworker, Camera camera, FoldedFigure foldedFigure, CreasePattern_Worker orite, PointSet otta_Men_zu, boolean i_fill) {
        boolean flipped = camera.isCameraMirrored();

        Point t_ob = new Point();
        Point t_tv = new Point();

        Memo memo_temp = new Memo();

        String str_stroke;
        str_stroke = "black";
        String str_strokewidth;
        str_strokewidth = "1";
        String str_fill;
        str_fill = "";

        SortingBox<Integer> nbox = ctworker.rating2();

        //面を描く準備

        //BigDecimalのコンストラクタの引数は浮動小数点数型と文字列型どちらもok。引数が浮動小数点数型は誤差が発生。正確な値を扱うためには、引数は文字列型で指定。

        for (int i_nbox = 1; i_nbox <= otta_Men_zu.getNumFaces(); i_nbox++) {
            int im;
            if (camera.getCameraMirror() == -1.0) {//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
                im = nbox.backwardsGetValue(i_nbox);
            } else {
                im = nbox.getValue(i_nbox);
            }

            StringBuilder text;//文字列処理用のクラスのインスタンス化

            text = new StringBuilder("M ");
            t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, 1)));
            t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, 1)));
            t_tv.set(camera.object2TV(t_ob));
            BigDecimal b_t_tv_x = new BigDecimal(String.valueOf(t_tv.getX()));
            BigDecimal b_t_tv_y = new BigDecimal(String.valueOf(t_tv.getY()));

            text.append(b_t_tv_x.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");


            for (int i = 2; i <= otta_Men_zu.getPointsCount(im); i++) {
                text.append("L ");
                t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, i)));
                t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, i)));
                t_tv.set(camera.object2TV(t_ob));
                BigDecimal b_t_tv_x_i = new BigDecimal(String.valueOf(t_tv.getX()));
                BigDecimal b_t_tv_y_i = new BigDecimal(String.valueOf(t_tv.getY()));

                text.append(b_t_tv_x_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");
            }

            text.append("Z");

            if (!i_fill) {
                str_fill = "none";

            } else {

                if (orite.getIFacePosition(im) % 2 == 1) {
                    str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                }
                if (orite.getIFacePosition(im) % 2 == 0) {
                    str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                }

                if (flipped) {
                    if (orite.getIFacePosition(im) % 2 == 1) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                    }
                    if (orite.getIFacePosition(im) % 2 == 0) {
                        str_fill = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                    }
                }
            }

            memo_temp.addLine("<path d=\"" + text + "\"" +
                    " style=\"" + "stroke:" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" +
                    " fill=\"" + str_fill + "\"" + " />"
            );
        }

        return memo_temp;
    }


    public static Memo getMemo_for_svg_with_camera(Camera camera, FoldedFigure foldedFigure, CreasePattern_Worker orite, PointSet subFace_figure) {//折り上がり図(hyouji_flg==5)
        boolean front_back = camera.isCameraMirrored();

        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        Memo memo_temp = new Memo();

        Point a = new Point();
        Point b = new Point();
        StringBuilder str_zahyou;
        String str_stroke = "black";
        String str_strokewidth = "1";

        int SubFaceTotal = subFace_figure.getNumFaces();
        SubFace[] s0 = foldedFigure.ct_worker.s0;

        //面を描く-----------------------------------------------------------------------------------------------------
        int[] x = new int[100];
        int[] y = new int[100];

        //SubFaceの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= SubFaceTotal; im++) { //SubFaceから上からの指定した番目の面のidを求める。
            s0[im].set_FaceId2fromTop_counted_position(foldedFigure.ct_worker.hierarchyList);//s0[]はSubFace_zuから得られるSubFaceそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った

        //面を描く
        int face_order;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //Determine the color of the imth SubFace when drawing a fold-up diagram
                face_order = 1;
                if (front_back) {
                    face_order = s0[im].getFaceIdCount();
                }


                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getFrontColor());
                }//g.setColor(F_color)
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                    str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getBackColor());
                }//g.setColor(B_color)

                if (front_back) {
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                        str_stroke = "yellow";
                    }//g.setColor(F_color)
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                        str_stroke = "gray";
                    }//g.setColor(B_color)
                }

                //折り上がり図を描くときのSubFaceの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = (int) t1.getX();
                    y[i] = (int) t1.getY();
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = (int) t1.getX();
                y[0] = (int) t1.getY();
                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求めるのはここまで

                str_zahyou = new StringBuilder(x[0] + "," + y[0]);
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    str_zahyou.append(" ").append(x[i]).append(",").append(y[i]);

                }

                memo_temp.addLine("<polygon points=\"" + str_zahyou + "\"" +
                        " style=\"" + "stroke:" + str_stroke + ";fill:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }
        //面を描く　ここまで-----------------------------------------------------------------------------------------


        //棒を描く-----------------------------------------------------------------------------------------

        str_stroke = StringOp.toHtmlColor(foldedFigure.foldedFigureModel.getLineColor());

        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
            int faceId_min, faceId_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int faceOrderMin, faceOrderMax;//PC画面に表示したときSubFace(faceId_min) で見える面の番号がMen_jyunban_min、SubFace(faceId_max) で見える面の番号がMen_jyunban_max
            boolean drawing_flg;

            drawing_flg = false;
            faceId_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            faceId_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

            if (s0[faceId_min].getFaceIdCount() == 0) {
                drawing_flg = true;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[faceId_max].getFaceIdCount() == 0) {
                drawing_flg = true;
            } else if (faceId_min == faceId_max) {
                drawing_flg = true;
            }//一本の棒の片面だけにSubFace有り
            else {
                faceOrderMin = 1;
                if (front_back) {
                    faceOrderMin = s0[faceId_min].getFaceIdCount();
                }
                faceOrderMax = 1;
                if (front_back) {
                    faceOrderMax = s0[faceId_max].getFaceIdCount();
                }
                if (s0[faceId_min].fromTop_count_FaceId(faceOrderMin) != s0[faceId_max].fromTop_count_FaceId(faceOrderMax)) {
                    drawing_flg = true;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (drawing_flg) {//棒を描く。
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));

                a.set(s_tv.getA());
                b.set(s_tv.getB());

                BigDecimal b_ax = new BigDecimal(String.valueOf(a.getX()));
                BigDecimal b_ay = new BigDecimal(String.valueOf(a.getY()));
                BigDecimal b_bx = new BigDecimal(String.valueOf(b.getX()));
                BigDecimal b_by = new BigDecimal(String.valueOf(b.getY()));

                memo_temp.addLine("<line x1=\"" + b_ax.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " y1=\"" + b_ay.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " x2=\"" + b_bx.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " y2=\"" + b_by.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " style=\"" + "stroke:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }


        return memo_temp;
    }


    public static Memo getMemoForFoldedFigure(FoldedFigure foldedFigure) {
        Memo memo_temp = new Memo();

        //Wire diagram svg
        if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
            memo_temp.addMemo(getMemo_wirediagram_for_svg_export(foldedFigure.ct_worker, foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), false));//If the fourth integer is 0, only the frame of the face is painted, and if it is 1, the face is painted.
        }

        //Folded figure (table) svg
        if (((foldedFigure.ip4 == FoldedFigure.State.FRONT_0) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(getMemo_wirediagram_for_svg_export(foldedFigure.ct_worker, foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), true));
            }

            //折り上がり図のsvg*************
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                memo_temp.addMemo(getMemo_for_svg_with_camera(foldedFigure.foldedFigureFrontCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker3.get()));// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        //折りあがり図（裏）のsvg
        if (((foldedFigure.ip4 == FoldedFigure.State.BACK_1) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            //透過図のsvg
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(getMemo_wirediagram_for_svg_export(foldedFigure.ct_worker, foldedFigure.foldedFigureRearCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker2.get(), true));
            }

            //折り上がり図のsvg*************
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                memo_temp.addMemo(getMemo_for_svg_with_camera(foldedFigure.foldedFigureRearCamera, foldedFigure, foldedFigure.cp_worker1, foldedFigure.cp_worker3.get()));// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        return memo_temp;
    }

    public static Memo getMemo_for_svg_export_with_camera(FoldLineSet foldLineSet, Camera camera, boolean i_cp_display, float fCreasePatternLineWidth, int lineWidth, LineStyle lineStyle, int pointSize) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Memo memo_temp = new Memo();

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();

        String str_stroke;
        String str_strokewidth = Integer.toString(lineWidth);

        //Drawing of crease pattern Polygonal lines other than auxiliary live lines
        if (i_cp_display) {
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineColor color = foldLineSet.getColor(i);
                if (color.isFoldingLine()) {
                    switch (color) {
                        case BLACK_0:
                            str_stroke = "black";
                            break;
                        case RED_1:
                            str_stroke = "red";
                            break;
                        case BLUE_2:
                            str_stroke = "blue";
                            break;
                        default:
                            throw new IllegalStateException("Not a folding line: " + color);
                    }

                    if (lineStyle == LineStyle.BLACK_TWO_DOT || lineStyle == LineStyle.BLACK_ONE_DOT) {
                        str_stroke = "black";
                    }

                    String str_stroke_dasharray;
                    switch (lineStyle) {
                        case COLOR:
                            str_stroke_dasharray = "";
                            break;
                        case COLOR_AND_SHAPE:
                        case BLACK_ONE_DOT:
                            //基本指定A　　線の太さや線の末端の形状
                            //dash_M1,一点鎖線
                            switch (color) {
                                case RED_1:
                                    str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3\"";
                                    break;
                                case BLUE_2:
                                    str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                                    break;
                                default:
                                    str_stroke_dasharray = "";
                                    break;
                            }
                            break;
                        case BLACK_TWO_DOT:
                            //基本指定A　　線の太さや線の末端の形状
                            //dash_M2,二点鎖線
                            switch (color) {
                                case RED_1:
                                    str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3 3 3\"";
                                    break;
                                case BLUE_2:
                                    str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                                    break;
                                default:
                                    str_stroke_dasharray = "";
                                    break;
                            }
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    s_tv.set(camera.object2TV(foldLineSet.get(i)));
                    a.set(s_tv.getA());
                    b.set(s_tv.getB());//a.set(s_tv.getax()+0.000001,s_tv.getay()+0.000001); b.set(s_tv.getbx()+0.000001,s_tv.getby()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    BigDecimal b_ax = new BigDecimal(String.valueOf(a.getX()));
                    double x1 = b_ax.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_ay = new BigDecimal(String.valueOf(a.getY()));
                    double y1 = b_ay.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_bx = new BigDecimal(String.valueOf(b.getX()));
                    double x2 = b_bx.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    BigDecimal b_by = new BigDecimal(String.valueOf(b.getY()));
                    double y2 = b_by.setScale(2, RoundingMode.HALF_UP).doubleValue();

                    memo_temp.addLine("<line x1=\"" + x1 + "\"" +
                            " y1=\"" + y1 + "\"" +
                            " x2=\"" + x2 + "\"" +
                            " y2=\"" + y2 + "\"" +
                            " " + str_stroke_dasharray + " " +
                            " stroke=\"" + str_stroke + "\"" +
                            " stroke-width=\"" + str_strokewidth + "\"" + " />");

                    if (pointSize != 0) {
                        if (fCreasePatternLineWidth < 2.0f) {//Draw a black square at the vertex

                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:none\"" +
                                    " width=\"" + 2.0 * (double) pointSize + "\"" +
                                    " height=\"" + 2.0 * (double) pointSize + "\"" +
                                    " x=\"" + (x1 - (double) pointSize) + "\"" +
                                    " y=\"" + (y1 - (double) pointSize) + "\"" +
                                    " />");

                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:none\"" +
                                    " width=\"" + 2.0 * (double) pointSize + "\"" +
                                    " height=\"" + 2.0 * (double) pointSize + "\"" +
                                    " x=\"" + (x2 - (double) pointSize) + "\"" +
                                    " y=\"" + (y2 - (double) pointSize) + "\"" +
                                    " />");
                        }
                    }

                    if (fCreasePatternLineWidth >= 2.0f) {//  Thick line
                        if (pointSize != 0) {
                            double d_width = (double) fCreasePatternLineWidth / 2.0 + (double) pointSize;

                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_width + "\"" +
                                    " cx=\"" + x1 + "\"" +
                                    " cy=\"" + y1 + "\"" +
                                    " />");

                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_width + "\"" +
                                    " cx=\"" + x2 + "\"" +
                                    " cy=\"" + y2 + "\"" +
                                    " />");
                        }
                    }
                }
            }
        }

        return memo_temp;
    }
}