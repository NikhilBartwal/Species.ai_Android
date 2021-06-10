import numpy as np
from PIL import Image
import streamlit as st
import tensorflow as tf

@st.cache
def get_labels(category):
    label_path = f'Project/app/src/main/assets/{category}_labels.txt'
    file = open(label_path).readlines()
    labels = [data.split('\n')[0] for data in file]
    return labels

@st.cache
def load_model(category):
    tflite_model_path = f'Project/app/src/main/assets/{category}_model.tflite'
    interpreter = tf.lite.Interpreter(tflite_model_path)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    return interpreter, input_details, output_details

@st.cache
def run_inference(model, image, input_details, output_details, fun_mode):
    expected_dims = input_details[0]['shape'][1:3]
    image = image.resize(expected_dims, Image.ANTIALIAS)

    #Taking only 3 channels in case of any png image with 4 channels
    image = np.asarray(image, dtype=np.float32)[:,:,:3]/255.0
    image = image.reshape([1, image.shape[0], image.shape[1], 3])

    model.set_tensor(input_details[0]['index'], image)
    model.invoke()

    species = model.get_tensor(output_details[0]['index'])
    return species

def display_inference(species_data, labels):
    species_data = species_data.flatten()
    top_indices = species_data.argsort()[-3:][::-1]
    top_scores = [round(species_data[index]*100, 2) for index in top_indices]
    top_preds = [labels[index] for index in top_indices]

    col_1, col_2, col_3 = st.beta_columns(3)
    with col_1:
        st.write("Species:", top_preds[0])
        st.write("Probability:", str(top_scores[0]), "%")
    with col_2:
        st.write("Species:", top_preds[1])
        st.write("Probability:", str(top_scores[1]), "%")
    with col_3:
        st.write("Species:", top_preds[2])
        st.write("Probability:", str(top_scores[2]), "%")
