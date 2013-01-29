/* 
 * Copyright (C) 2013  Nastaran Shafiei and Franck van Breugel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can find a copy of the GNU General Public License at
 * <http://www.gnu.org/licenses/>.
 */

package nhandler.conversion;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MJIEnv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nhandler.util.ValueIdentityHashMap;

/**
 * Converts objects and classes from JPF to JVM, and from JVM to JPF.
 * 
 * @author Nastaran Shafiei
 * @author Franck van Breugel
 */
public class Converter {

  MJIEnv env;

  JPF2JVM jpf2jvm;

  JVM2JPF jvm2jpf;

  /**
   * Keeps track of the JVM objects that have been already created from their
   * corresponding JPF objects, while performing conversion from JPF to JVM
   */
  static ValueIdentityHashMap<Integer, Object> objMapJPF2JVM = new ValueIdentityHashMap<Integer, Object>();

  /**
   * Keeps track of the JVM classes that have been already created from their
   * corresponding JPF classes, while performing conversion from JPF to JVM
   */
  static ValueIdentityHashMap<Integer, Class<?>> classMapJPF2JVM = new ValueIdentityHashMap<Integer, Class<?>>();

  /**
   * Keeps track of the JPF objects that have been already updated from their
   * corresponding JVM objects, while performing conversion from JVM to JPF
   */
  static HashMap<Integer, Object> updatedJPFObj = new ValueIdentityHashMap<Integer, Object>();

  /**
   * Keeps track of the JPF classes that have been already updated from their
   * corresponding JVM classes, while performing conversion from JVM to JPF
   */
  static Set<Integer> updatedJPFCls = new HashSet<Integer>();

  static boolean resetState;

  public Converter (MJIEnv env) {
    this.env = env;

    resetState = env.getConfig().getBoolean("nhandler.resetVMState");

    if (resetState) {
      // these are reset on-demond by setting the nhandler.resetVMState
      // property in the properties file
      objMapJPF2JVM.clear();
      classMapJPF2JVM.clear();
    }

    // these always need to be reset
    updatedJPFObj.clear();
    updatedJPFCls.clear();

    this.jpf2jvm = new JPF2JVM(env);
    this.jvm2jpf = new JVM2JPF(env);
  }

  /********** Conversion from JPF to JVM ***********/
  /**
   * Returns a new JVM Class object corresponding to the given JPF class. If
   * such a Class object already exists, it is returned. Otherwise a new one is
   * created.
   * 
   * @param JPFRef
   *          an integer representing a JPF class
   * 
   * @return a JVM Class object corresponding to the given JPF class, JPFRef
   * 
   * @throws ConversionException
   *           if any incorrect input parameter is observed during the
   *           conversion
   */
  public Class<?> getJVMCls (int JPFRef) throws ConversionException {
    return this.jpf2jvm.getJVMCls(JPFRef);
  }

  /**
   * Returns a JVM object corresponding to the given JPF object. If such an
   * object already exists, it is returned. Otherwise a new one is created.
   * 
   * @param JPFRef
   *          an integer representing a JPF object
   * 
   * @return a JVM object corresponding to the given JPF object, JPFRef
   * 
   * @throws ConversionException
   *           if any incorrect input parameter is observed during the
   *           conversion
   */
  public Object getJVMObj (int JPFRef) throws ConversionException {
    return this.jpf2jvm.getJVMObj(JPFRef);
  }

  /********** Conversion from JVM to JPF ***********/
  /**
   * Returns a JPF class corresponding to the given JVM Class object. If such an
   * class exists, it is updated (if it has not been updated) corresponding to
   * the given JVMObj. Otherwise a new JPF class corresponding to the given JVM
   * class object is created and added to the list of the JPF loaded classes.
   * 
   * @param JVMCls
   *          a JVM Class object
   * 
   * @return a JPF class corresponding to the given JVM class, JVMCls
   * 
   * @throws ConversionException
   *           if any incorrect input parameter is observed during the
   *           conversion
   */
  public ClassInfo getJPFCls (Class<?> JVMCls) throws ConversionException {
    return this.jvm2jpf.getJPFCls(JVMCls);
  }

  /**
   * Returns a JPF object corresponding to the given JVM object. If such an
   * object exists, it is updated (if it has not been updated) corresponding to
   * the given JVMObj. Otherwise a new JPF object corresponding to the given JVM
   * object is created.
   * 
   * @param JVMObj
   *          a JVM object
   * 
   * @return a JPF object corresponding to the given JVM object, JVMObj
   * 
   * @throws ConversionException
   *           if any incorrect input parameter is observed during the
   *           conversion
   */
  public int getJPFObj (Object JVMObj) throws ConversionException {
    return this.jvm2jpf.getJPFObj(JVMObj);
  }

  /**
   * Update the given JPF object according to the given JVM object. For the case
   * of the non-array object, its JPF class is also updated according to the
   * class of the given JVM object.
   * 
   * @param JVMObj
   *          a JVM object
   * @param JPFObj
   *          a JPF object
   * 
   * @throws ConversionException
   *           if any incorrect input parameter is observed during the
   *           conversion
   */
  public void updateJPFObj (Object JVMObj, int JPFObj) throws ConversionException {
    this.jvm2jpf.updateJPFObj(JVMObj, JPFObj);
  }
}