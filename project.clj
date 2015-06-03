(defproject pst "0.1.0"
  :description "Processes MS Exchange archives."
  :url "https://github.com/firstlook/pst"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.pff/java-libpst "0.8.1"]]
  :main pst.core
  :signing {:gpg-key "DC284680"})
