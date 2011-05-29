(ns ewk.core
  (:require [clojure.contrib.logging :as lg])
  (:require [clojure.contrib.string  :as string])
  (:require [net.cgrand.enlive-html  :as html])
  (:require [ewk.features])
  (:require [clj-ml.classifiers :as classifiers])
  (:require [clj-ml.data :as data])
  (:use clojure.pprint)
  (:use clojure.contrib.json)
  (:gen-class))

(defn- create-dataset
  "Convert the dataset map into an instance of a weka dataset."
  [[{features :features} :as dataset]]
  (let
    [new-dataset (data/make-dataset "pages" (conj (keys features)
                                   {:class (vec (set
                                             (map 
                                               #(keyword (% :class))
                                               dataset)))})
                     (map #(vals (% :features)) dataset))]
    (do
      (data/dataset-set-class new-dataset :class)
      new-dataset)))

(defn train-model
  "Given a clj-ml dataset, create and train a classifier."
  [vec-map-dataset]
  (let [classifier (classifiers/make-classifier :regression :logistic)
        dataset (create-dataset vec-map-dataset)]
    (do
      (classifiers/classifier-train classifier dataset)
      classifier)))

<<<<<<< HEAD
(defn classify-instance
  "Given a dataset, a model and an instance, return the model's guess at the
  instance's class."
  [vec-map-dataset model map-instance]
  (let [dataset (create-dataset vec-map-dataset)
        instance (data/make-instance dataset
                                     (conj (vals (map-instance :features))
                                      (map-instance :class)))]
    (.value (.classAttribute instance)
            (classifiers/classifier-classify model instance))))
=======
(def example-dataset 
     [{:file "index.html" :class "index" :features { :is_home 1.0 :a 1.0 } } 
      {:file "index.html" :class "product detail" :features { :is_home 0.0 :a 3.0 } } ])

>>>>>>> 7a884a8a09499710ac7b259e9425f1fd5bbe4682

(defn- test-train-model
  "Check that a simple case works as expected"
  []
  (let [example-dataset 
        [{:file "index.html" :class "home" :features { :is_home 1.0}} 
         {:file "index.html" :class "product detail" :features { :is_home 0.0}}]]
<<<<<<< HEAD
    (let [model (train-model example-dataset)]
      (= "home"
         (classify-instance example-dataset model
            {:file "test.html" :class "home" :features {:is_home 1.0}})))))

(defn cross-validate-model
  "Check that a model is accurate by comparing its classifications of the
  training dataset to the training data."
  [vec-map-dataset model]
  (map #(= (:class %)
           (classify-instance vec-map-dataset model %)) vec-map-dataset))
=======
    (let [model (train-model example-dataset)
          test-instance (create-instance example-dataset
                            {:file "test.html" :class "home" :features {:is_home 1.0}})]
      (when (= (.value (.classAttribute test-instance)
		       (classifiers/classifier-classify model test-instance))
	       ("home"))))))
>>>>>>> 7a884a8a09499710ac7b259e9425f1fd5bbe4682

(defn compute-document-features
  "Evaluates the feature functions defined in ewk.features for the provided
  html. Returns a map of feature name to result."
  [html]
  (let [features   (ns-publics 'ewk.features) 
	html-input (html/html-snippet html)]
    (zipmap
      (map keyword (keys features))
      (map #(% html-input) (vals features)))))

(defn -main [& args]
  (lg/info (str "Running with args: " (string/join " " args))))

