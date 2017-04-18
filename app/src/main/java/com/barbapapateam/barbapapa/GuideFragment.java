package com.barbapapateam.barbapapa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;


public class GuideFragment extends Fragment implements View.OnClickListener {

    //Zone de texte contenant la question
    TextView t1;

    //On charge les bière dans une liste, et en fonction des réponses aux questions, on supprimera des éléments de la liste
    LinkedList<Beer> beers;

    public GuideFragment() {
        // Required empty public constructor
    }

    //On distinguera les questions en questions principales (question1, 2 et 3) et questions secondaires (contenue des question1, 2 et 3)
    String[] question1 = {"Voulez vous une bière en Pression ?", "Donc plutôt en bouteille ?"};
    String[] question2 = {"Voulez vous une bière Blonde ?", "Plutôt blanche donc ?" ,"Plutôt ambrée du coup ? ", "Non plus ? Plutôt brune alors ?"};
    String[] question3 = {"Voulez vous une bière forte ?", "On part sur une bière plutôt douce donc ?"};

    private String[][] questions = {question1, question2, question3};

    private boolean[] bottle = {true, false};
    private String[] color = {"Blonde", "Blanche", "Ambrée", "Brune"};
    private String[] degre = {"forte", "douce"};

    private String[][] attributes = {{},color, degre};
    private int indice1 = 0 ;
    private int indice2 = 0 ;
    String question = questions[0][0];


    //Affiche le résultat de la recherche
    private LinkedList<Beer> getResult(){
        //Affiche la liste des bières correspondantes
        return beers;
    }

    //Fonction proposant la bière la mieux notée correspondant aux goûts de l'utilisateur
    public Beer getMyBeer(){
        LinkedList<Beer> listResult = getResult();
        Beer myBeer = listResult.getFirst();
        if(myBeer == null){
            //Afficher un message comme quoi il n'y a aucune réponse
        } else {
            for (int i = 1; i< listResult.size(); i++){
                if(listResult.get(i).getNote()> myBeer.getNote()){
                    myBeer = listResult.get(i);
                }
            }
        }
        return myBeer;
    }

    //Permet de passer à la question principale suivante
    private void getNextQuestion(){
        indice1++;
        if(indice1 == 3){
            LaunchCommandActivity(getMyBeer());
        } else {
            indice2 = 0;
            question = questions[indice1][indice2];
            t1.setText(question);
        }
    }

    //Permet de passer à la question principale précédente.
    private void getPreviousQuestion(){
        indice1--;
        if (indice1 == -1) {
            indice1 = 0;
        } else {
            indice2 = 0;
            question = questions[indice1][indice2];
            t1.setText(question);
        }
    }

    //Permet de passer à la question secondaire suivante.
    private void getNextNuance(){
        indice2++;
        if (indice1 == 0) {
            if (indice2 == 2) {
                indice2 = 0;
                question = questions[indice1][indice2];
            } else {
                question = questions[indice1][indice2];
            }
        } else if (indice1 == 1) {
            if (indice2 == 4) {
                indice2 = 0;
                question = questions[indice1][indice2];
            } else {
                question = questions[indice1][indice2];
            }
        } else {
            if(indice2 == 2 ){
                indice2 = 0;
                question = questions[indice1][indice2];
            } else {
                question = questions[indice1][indice2];
            }
        }
        t1.setText(question);
    }

    // L'utilisateur répond oui
    //Cette fonction seulement supprimera des éléments de la liste
    private void yes(){
        //On supprime des éléments de la liste.
        if (indice1 == 2){
            for(int i = 0; i< beers.size(); i++){
                Beer beer = beers.get(i);
                //L'utilisateur veut une bière forte
                //Dans le cas du degré, on doit faire un prétraite car ABV est un float
                if(indice2 == 0)
                    if(beer.ABV <= 6)
                        beers.remove(i);
                        //L'utilisateur veut une bière douce.
                    else
                    if(beer.ABV >6)
                        beers.remove(i);
            }
        } else if (indice1 == 0) {
            //pour les autres question, les listes dans attributes correspondent a la valeur des attributs de Beer.
            for (int i = 0; i < beers.size(); i++) {
                Beer beer = beers.get(i);
                if (beer.bottle != bottle[indice2]){
                    beers.remove(i);
                }
            }
        } else if (indice1 == 1) { //pour les autres question, les listes dans attributes correspondent a la valeur des attributs de Beer.
            for (int i = 0; i < beers.size(); i++) {
                Beer beer = beers.get(i);
                if (beer.color.equals(attributes[indice1][indice2])){
                    beers.remove(i);
                }
            }
        }
        //On passe à la question suivante.
        getNextQuestion();
    }

    // L'utilisateur répond non, on passe à la question secondaire suivante
    private void no(){
        getNextNuance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.advanced_recommandation, container, false);

        //On charge les bière dans une liste, et en fonction des réponses aux questions, on supprimera des éléments de la liste
        LinkedList<Beer> beers = Utils.getBeersFromJSON("beers.json", getActivity().getApplicationContext());

        //Zone de texte contenant la question
        t1 = (TextView) view.findViewById(R.id.ARtextView);


        //connection des boutons entre model et view
        ImageButton noB = (ImageButton) view.findViewById(R.id.imageButtonCross);
        ImageButton yesB = (ImageButton) view. findViewById(R.id.imageButtonValid);
        ImageButton goBackB = (ImageButton) view.findViewById(R.id.imageButtonBack);

        noB.setOnClickListener(this);
        yesB.setOnClickListener(this);
        goBackB.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }


    private void LaunchCommandActivity(Beer beer) {

        Intent commandIntent = new Intent(this.getContext(), CommandActivity.class);
        commandIntent.putExtra("BEER", beer);
        startActivity(commandIntent);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageButtonCross:
                no();
                break;

            case R.id.imageButtonValid:
                yes();
                break;

            case R.id.imageButtonBack:
                getPreviousQuestion();
                break;
        }
    }
}
