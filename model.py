import numpy as np
from PIL import Image
import streamlit as st
import tensorflow as tf
from database_utils import *

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
def run_inference(model, image, input_details, output_details):
    expected_dims = input_details[0]['shape'][1:3]
    image = image.resize(expected_dims, Image.ANTIALIAS)

    #Taking only 3 channels in case of any png image with 4 channels
    image = np.asarray(image, dtype=np.float32)[:,:,:3]/255.0
    image = image.reshape([1, image.shape[0], image.shape[1], 3])

    model.set_tensor(input_details[0]['index'], image)
    model.invoke()

    species = model.get_tensor(output_details[0]['index'])
    return species

def display_inference(species_data, labels, category, image):
    species_data = species_data.flatten()
    top_indices = species_data.argsort()[-3:][::-1]
    top_scores = [round(species_data[index]*100, 2) for index in top_indices]
    top_preds = [labels[index] for index in top_indices]

    st.write(" ----- ")
    pred_1_image, pred_1_info = st.beta_columns([1,2])
    info_dict_1 = get_info(top_preds[0], category)
    info_dict_1['score'] = top_scores[0]
    with pred_1_image:
        st.image(image)
    with pred_1_info:
        display_info(info_dict_1)
    with st.beta_expander('Read more...'):
        st.write('\n'.join(info_dict_1['description'].split('.')))

    pred_2_image, pred_2_info = st.beta_columns([1,2])
    info_dict_2 = get_info(top_preds[1], category)
    info_dict_2['score'] = top_scores[1]
    with pred_2_image:
        st.image(image)
    with pred_2_info:
        display_info(info_dict_2)
    with st.beta_expander('Read more...'):
        st.write('\n'.join(info_dict_2['description'].split('.')))

    pred_3_image, pred_3_info = st.beta_columns([1,2])
    info_dict_3 = get_info(top_preds[2], category)
    info_dict_3['score'] = top_scores[2]
    with pred_3_image:
        st.image(image)
    with pred_3_info:
        display_info(info_dict_3)
    with st.beta_expander('Read more...'):
        st.write('\n'.join(info_dict_3['description'].split('.')))
