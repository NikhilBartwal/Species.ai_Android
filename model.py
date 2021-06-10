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
    image = np.asarray(image, dtype=np.float32)/255.0
    image = image.reshape([1, image.shape[0], image.shape[1], 3])

    model.set_tensor(input_details[0]['index'], image)
    model.invoke()

    species = model.get_tensor(output_details[0]['index'])
    return species

def display_inference(species_data, labels):
    pass
